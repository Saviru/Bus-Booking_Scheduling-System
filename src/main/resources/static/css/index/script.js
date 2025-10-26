const hamburger = document.querySelector('.hamburger');
const navMenu = document.querySelector('.nav-menu');
const themeToggle = document.getElementById('themeToggle');
const themeIcon = document.getElementById('themeIcon');

// ===== THEME MANAGEMENT =====
const getPreferredTheme = () => {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
        return savedTheme;
    }
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
};

const setTheme = (theme) => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
    
    if (theme === 'dark') {
        themeIcon.classList.remove('fa-sun');
        themeIcon.classList.add('fa-moon');
        themeIcon.style.color = '#ffffff';
    } else {
        themeIcon.classList.remove('fa-moon');
        themeIcon.classList.add('fa-sun');
        themeIcon.style.color = '#ffffff';
    }
};

// Initialize theme
setTheme(getPreferredTheme());

// Theme toggle click handler with optimized animation
themeToggle.addEventListener('click', () => {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    setTheme(newTheme);
    
    // Smooth rotation animation
    themeIcon.style.transition = 'transform 0.4s cubic-bezier(0.4, 0, 0.2, 1)';
    themeIcon.style.transform = 'rotate(360deg)';
    setTimeout(() => {
        themeIcon.style.transform = 'rotate(0deg)';
    }, 400);
});

// ===== MOBILE NAVIGATION =====
hamburger.addEventListener('click', () => {
    hamburger.classList.toggle('active');
    navMenu.classList.toggle('active');
    
    // Prevent body scroll when menu is open
    if (navMenu.classList.contains('active')) {
        document.body.style.overflow = 'hidden';
    } else {
        document.body.style.overflow = '';
    }
});

// Close mobile menu when clicking on a link
document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', () => {
        hamburger.classList.remove('active');
        navMenu.classList.remove('active');
        document.body.style.overflow = '';
    });
});

// ===== STATS COUNTER ANIMATION =====
const animateCounter = (element) => {
    const target = parseInt(element.getAttribute('data-target'));
    const duration = 2500;
    const startTime = performance.now();
    
    const updateCounter = (currentTime) => {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        
        // Easing function for smooth animation
        const easeOutQuart = 1 - Math.pow(1 - progress, 4);
        const current = Math.floor(easeOutQuart * target);
        
        element.textContent = current;
        
        if (progress < 1) {
            requestAnimationFrame(updateCounter);
        } else {
            element.textContent = target;
        }
    };
    
    requestAnimationFrame(updateCounter);
};

// ===== INTERSECTION OBSERVERS =====
const observerOptions = {
    threshold: 0.15,
    rootMargin: '0px 0px -80px 0px'
};

// Fade-in observer for general elements
const fadeInObserver = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
            fadeInObserver.unobserve(entry.target);
        }
    });
}, observerOptions);

// Scale-in observer for special elements
const scaleInObserver = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'scale(1)';
            scaleInObserver.unobserve(entry.target);
        }
    });
}, observerOptions);

// Stats counter observer
const statsObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            const statNumbers = entry.target.querySelectorAll('.stat-number');
            statNumbers.forEach(stat => {
                animateCounter(stat);
            });
            statsObserver.unobserve(entry.target);
        }
    });
}, observerOptions);

// ===== OBSERVE ELEMENTS FOR ANIMATIONS =====
// Fade-in animations for features and testimonials
document.querySelectorAll('.featuresAnim, .testimonial-card').forEach(el => {
    el.style.opacity = '0';
    el.style.transform = 'translateY(30px)';
    el.style.transition = 'all 0.8s cubic-bezier(0.4, 0, 0.2, 1)';
    fadeInObserver.observe(el);
});

// Feature cards with special animation
document.querySelectorAll('.feature-card').forEach(el => {
    el.style.opacity = '0';
    el.style.transform = 'translateY(30px)';
    el.style.transition = 'all 0.8s cubic-bezier(0.4, 0, 0.2, 1)';
    fadeInObserver.observe(el);
});

// Steps with scale animation
document.querySelectorAll('.step').forEach(el => {
    el.style.opacity = '0';
    el.style.transform = 'scale(0.95)';
    el.style.transition = 'all 0.8s cubic-bezier(0.4, 0, 0.2, 1)';
    scaleInObserver.observe(el);
});

// Observe howto container
const howtoContainer = document.querySelector('.howto-cont');
if (howtoContainer) {
    howtoContainer.style.opacity = '0';
    howtoContainer.style.transform = 'scale(0.95)';
    howtoContainer.style.transition = 'all 0.8s cubic-bezier(0.4, 0, 0.2, 1)';
    
    const howtoObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'scale(1)';
                howtoObserver.unobserve(entry.target);
            }
        });
    }, observerOptions);
    
    howtoObserver.observe(howtoContainer);
}

// Observe stats for counter animation
const statsSection = document.querySelector('.stats-section');
if (statsSection) {
    statsObserver.observe(statsSection);
}

// ===== SMOOTH SCROLL BEHAVIOR =====
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            const headerOffset = 100;
            const elementPosition = target.getBoundingClientRect().top;
            const offsetPosition = elementPosition + window.pageYOffset - headerOffset;
            
            window.scrollTo({
                top: offsetPosition,
                behavior: 'smooth'
            });
        }
    });
});

// ===== FEATURE ICON HOVER ANIMATION =====
document.querySelectorAll('.feature-card').forEach(card => {
    const icon = card.querySelector('.feature-icon i');
    if (icon) {
        card.addEventListener('mouseenter', () => {
            icon.style.transition = 'transform 0.5s cubic-bezier(0.4, 0, 0.2, 1)';
            icon.style.transform = 'scale(1.1) rotateY(360deg)';
        });
        
        card.addEventListener('mouseleave', () => {
            icon.style.transform = 'scale(1) rotateY(0deg)';
        });
    }
});

// ===== PAGE LOAD ANIMATION =====
window.addEventListener('load', () => {
    document.body.style.opacity = '1';
});

// Initial body opacity setup
document.body.style.opacity = '0';
document.body.style.transition = 'opacity 0.6s cubic-bezier(0.4, 0, 0.2, 1)';

// ===== SCROLL-BASED ANIMATIONS =====
let lastScrollY = window.pageYOffset;
const header = document.querySelector('.header');

// Parallax effect for sections
const parallaxSections = document.querySelectorAll('.stats-section, .features, .testimonials, .cta-section');

const handleScroll = () => {
    const currentScrollY = window.pageYOffset;
    
    // Header hide/show on scroll
    if (currentScrollY > lastScrollY && currentScrollY > 100) {
        header.style.transform = 'translateX(-50%) translateY(-100%)';
    } else {
        header.style.transform = 'translateX(-50%) translateY(0)';
    }
    lastScrollY = currentScrollY;
    
    // Parallax effect
    parallaxSections.forEach(section => {
        const rect = section.getBoundingClientRect();
        const scrollPercent = (window.innerHeight - rect.top) / window.innerHeight;
        
        if (scrollPercent > 0 && scrollPercent < 1) {
            const translateY = (scrollPercent - 0.5) * 20;
            section.style.transform = `translateY(${translateY}px)`;
        }
    });
};

// ===== PERFORMANCE OPTIMIZATION =====
// Throttle scroll events for better performance
let ticking = false;
window.addEventListener('scroll', () => {
    if (!ticking) {
        window.requestAnimationFrame(() => {
            handleScroll();
            ticking = false;
        });
        ticking = true;
    }
});

// ===== SMOOTH REVEAL ON SCROLL =====
const revealElements = document.querySelectorAll('.cta-content, .stats-content');
const revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.animation = 'fadeInUp 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards';
            revealObserver.unobserve(entry.target);
        }
    });
}, {
    threshold: 0.2,
    rootMargin: '0px 0px -50px 0px'
});

revealElements.forEach(el => {
    el.style.opacity = '0';
    revealObserver.observe(el);
});

console.log('✨ EZBus - Optimized Landing Page Loaded Successfully');
console.log('🎨 Smooth animations and transitions enabled');
console.log('⚡ Performance optimized with requestAnimationFrame');
console.log('🎭 Scroll-based animations activated');

// ===== MODAL MANAGEMENT =====
const loginModal = document.getElementById('loginModal');
const registerModal = document.getElementById('registerModal');
const openLoginBtn = document.getElementById('openLogin');
const openRegisterBtn = document.getElementById('openRegister');
const closeLoginBtn = document.getElementById('closeLogin');
const closeRegisterBtn = document.getElementById('closeRegister');
const loginOverlay = document.getElementById('loginOverlay');
const registerOverlay = document.getElementById('registerOverlay');
const switchToRegisterBtn = document.getElementById('switchToRegister');
const switchToLoginBtn = document.getElementById('switchToLogin');

// Password toggle functionality
const togglePasswordButtons = [
    { btn: document.getElementById('toggleLoginPassword'), input: document.getElementById('loginPassword') },
    { btn: document.getElementById('toggleRegisterPassword'), input: document.getElementById('registerPassword') },
    { btn: document.getElementById('toggleConfirmPassword'), input: document.getElementById('confirmPassword') }
];

togglePasswordButtons.forEach(({ btn, input }) => {
    if (btn && input) {
        btn.addEventListener('click', () => {
            const type = input.type === 'password' ? 'text' : 'password';
            input.type = type;
            const icon = btn.querySelector('i');
            icon.classList.toggle('fa-eye');
            icon.classList.toggle('fa-eye-slash');
        });
    }
});

// Open modals
if (openLoginBtn) {
    openLoginBtn.addEventListener('click', () => {
        loginModal.classList.add('active');
        document.body.classList.add('modal-open');
    });
}

if (openRegisterBtn) {
    openRegisterBtn.addEventListener('click', () => {
        registerModal.classList.add('active');
        document.body.classList.add('modal-open');
    });
}

// Close modals
const closeModal = (modal) => {
    modal.classList.remove('active');
    document.body.classList.remove('modal-open');
};

if (closeLoginBtn) {
    closeLoginBtn.addEventListener('click', () => closeModal(loginModal));
}

if (closeRegisterBtn) {
    closeRegisterBtn.addEventListener('click', () => closeModal(registerModal));
}

if (loginOverlay) {
    loginOverlay.addEventListener('click', () => closeModal(loginModal));
}

if (registerOverlay) {
    registerOverlay.addEventListener('click', () => closeModal(registerModal));
}

// Switch between modals
if (switchToRegisterBtn) {
    switchToRegisterBtn.addEventListener('click', (e) => {
        e.preventDefault();
        closeModal(loginModal);
        setTimeout(() => {
            registerModal.classList.add('active');
            document.body.classList.add('modal-open');
        }, 300);
    });
}

if (switchToLoginBtn) {
    switchToLoginBtn.addEventListener('click', (e) => {
        e.preventDefault();
        closeModal(registerModal);
        setTimeout(() => {
            loginModal.classList.add('active');
            document.body.classList.add('modal-open');
        }, 300);
    });
}

// Close modal on ESC key
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        if (loginModal.classList.contains('active')) {
            closeModal(loginModal);
        }
        if (registerModal.classList.contains('active')) {
            closeModal(registerModal);
        }
    }
});

window.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const action = urlParams.get('action');

    if (action === 'login') {
        loginModal.classList.add('active');
        document.body.classList.add('modal-open');
        // Clean URL without reloading
        history.replaceState(null, '', window.location.pathname);
    } else if (action === 'register' || action === 'signup') {
        registerModal.classList.add('active');
        document.body.classList.add('modal-open');
        // Clean URL without reloading
        history.replaceState(null, '', window.location.pathname);
    }
});


