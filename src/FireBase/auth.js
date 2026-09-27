import { auth, db } from "./config";
import {
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signInWithPopup,
  GoogleAuthProvider,
  GithubAuthProvider,
  OAuthProvider,
  signOut,
  onAuthStateChanged,
  sendPasswordResetEmail,
  setPersistence,
  browserLocalPersistence,
  browserSessionPersistence,
} from "firebase/auth";
import { collection, doc, getDoc, getDocs, limit, query, setDoc, where } from "firebase/firestore";

const googleProvider   = new GoogleAuthProvider();
const githubProvider   = new GithubAuthProvider();
const microsoftProvider = new OAuthProvider("microsoft.com");

const normalizeEmail = (email = "") => email.trim().toLowerCase();

const fetchUserData = async (uid) => {
  const snap = await getDoc(doc(db, "usuarios", uid));
  return snap.exists() ? snap.data() : null;
};

const fetchUserDataByEmail = async (email) => {
  const normalizedEmail = normalizeEmail(email);
  if (!normalizedEmail) return null;

  const snap1 = await getDocs(
    query(collection(db, "usuarios"), where("email", "==", normalizedEmail), limit(1)),
  );
  if (!snap1.empty) return snap1.docs[0].data();

  const snap2 = await getDocs(
    query(collection(db, "usuarios"), where("correo", "==", normalizedEmail), limit(1)),
  );
  return snap2.empty ? null : snap2.docs[0].data();
};

export const fetchUserDataForAuth = async (firebaseUser) => {
  if (!firebaseUser?.uid) return null;

  const byUid = await fetchUserData(firebaseUser.uid);
  if (byUid) return byUid;

  return fetchUserDataByEmail(firebaseUser.email);
};

export const fetchRolByUid = async (firebaseUser) => {
  if (!firebaseUser?.uid) return null;
  const userData = await fetchUserDataForAuth(firebaseUser);
  return userData?.rol ?? null;
};

const createSocialUserIfMissing = async (firebaseUser) => {
  const existingUser = await fetchUserDataForAuth(firebaseUser);
  if (existingUser) return existingUser;

  const newUser = {
    uid: firebaseUser.uid,
    email: normalizeEmail(firebaseUser.email),
    nombre: firebaseUser.displayName || firebaseUser.email,
    rol: "usuario",
  };
  await setDoc(doc(db, "usuarios", firebaseUser.uid), newUser);
  return newUser;
};

export const signIn = async (email, password) => {
  const res = await signInWithEmailAndPassword(auth, normalizeEmail(email), password);

  const userData = await fetchUserDataForAuth(res.user);
  return { user: res.user, userData };
};

export const signInGoogle = async () => {
  const res = await signInWithPopup(auth, googleProvider);
  const userData = await createSocialUserIfMissing(res.user);
  return { user: res.user, userData };
};

export const signInMicrosoft = async () => {
  const res = await signInWithPopup(auth, microsoftProvider);
  const userData = await createSocialUserIfMissing(res.user);
  return { user: res.user, userData };
};

export const signInGithub = async () => {
  const res = await signInWithPopup(auth, githubProvider);
  const userData = await createSocialUserIfMissing(res.user);
  return { user: res.user, userData };
};

export const register = async (email, password, nombre) => {
  const normalizedEmail = normalizeEmail(email);
  const existingUser = await fetchUserDataByEmail(normalizedEmail);
  if (existingUser) {
    const error = new Error("Ya existe una cuenta con ese correo.");
    error.code = "auth/email-already-in-use";
    throw error;
  }

  const res = await createUserWithEmailAndPassword(auth, normalizedEmail, password);
  const userData = { uid: res.user.uid, email: normalizedEmail, nombre, rol: "usuario" };
  await setDoc(doc(db, "usuarios", res.user.uid), userData);
  return { user: res.user, userData };
};

// "Recordar en este equipo": sesión permanente o solo mientras la pestaña esté abierta
export const setRecordarSesion = (recordar) =>
  setPersistence(auth, recordar ? browserLocalPersistence : browserSessionPersistence);

export const doSignOut  = async () => { await signOut(auth); };
export const resetPassword = async (email) => { await sendPasswordResetEmail(auth, email); };
export const onAuthChange  = (callback) => onAuthStateChanged(auth, callback);
