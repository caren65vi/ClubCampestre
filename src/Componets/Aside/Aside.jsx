import React from 'react'
import './Aside.css'
import whatsappIcon from '../../assets/whatsapp.svg'
import instagramIcon from '../../assets/instagram.svg'
import emailIcon from '../../assets/email.svg'

const Aside = () => {
    return (
        <div>
            <aside className="socialAside">
                <a className="socialAsideLink socialAsideWhatsapp" href="https://wa.me/573243376439" target="_blank" rel="noreferrer noopener">
                    <img src={whatsappIcon} alt="WhatsApp" />
                </a>
                <a className="socialAsideLink socialAsideInstagram" href="https://www.instagram.com/hotel.lascoralinasoficial" target="_blank" rel="noreferrer noopener">
                    <img src={instagramIcon} alt="Instagram" />
                </a>
                <a className="socialAsideLink socialAsideEmail" href="mailto:coralinas1515@gmail.com">
                    <img src={emailIcon} alt="Email" />
                </a>
            </aside>
        </div>
    )
}

export default Aside
