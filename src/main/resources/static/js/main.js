// Script para el efecto de header "glass"
// Al hacer scroll (window.scrollY > 50) se agrega la clase .scrolled al header,
// que activa el fondo translúcido, el blur y el borde inferior definidos en css/styles.css.
// Si el usuario vuelve arriba del todo, la clase se elimina y el header regresa a su estado base.
window.addEventListener('scroll', () => {
        const header = document.getElementById('main-header');
        if (window.scrollY > 50) {
                header.classList.add('scrolled');
        } else {
                header.classList.remove('scrolled');
        }
});

// Navegación del carrusel "Nuestro equipo"
// Las flechas desplazan el carrusel horizontalmente una fracción de su ancho visible.
const teamCarousel = document.getElementById('team-carousel');
const teamPrev = document.getElementById('team-prev');
const teamNext = document.getElementById('team-next');

if (teamCarousel && teamPrev && teamNext) {
        const teamScrollStep = () => teamCarousel.clientWidth * 0.6;

        teamPrev.addEventListener('click', () => {
                teamCarousel.scrollBy({ left: -teamScrollStep(), behavior: 'smooth' });
        });

        teamNext.addEventListener('click', () => {
                teamCarousel.scrollBy({ left: teamScrollStep(), behavior: 'smooth' });
        });
}
