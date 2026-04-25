import React from 'react'
import './Aside.css'
import whatsappIcon from '../../assets/whatsapp.svg'
import instagramIcon from '../../assets/instagram.svg'
import emailIcon from '../../assets/email.svg'

const Aside = () => {
    return (
        <div>

            <aside className="social-aside">
                <a className="social-aside__link social-aside__whatsapp" href="https://wa.me/573243376439" target="_blank" rel="noreferrer noopener">
                    <img src={whatsappIcon} alt="WhatsApp" />
                </a>
                <a className="social-aside__link social-aside__instagram" href="https://www.instagram.com/hotel.lascoralinasoficial" target="_blank" rel="noreferrer noopener">
                    <img src={instagramIcon} alt="Instagram" />
                </a>
                <a className="social-aside__link social-aside__email" href="mailto:coralinas1515@gmail.com">
                    <img src={emailIcon} alt="Email" />
                </a>
            </aside>

        </div>
    )
}

export default Aside
