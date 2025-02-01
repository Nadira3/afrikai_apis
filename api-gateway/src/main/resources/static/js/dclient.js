// Example: Smooth Scroll to Sections
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
  anchor.addEventListener('click', function (e) {
    e.preventDefault();
    document.querySelector(this.getAttribute('href')).scrollIntoView({
      behavior: 'smooth'
    });
  });
});

// FAQ JavaScript (faq.js)
document.querySelectorAll('.faq-question').forEach((faqButton) => {
  faqButton.addEventListener('click', () => {
    const answer = faqButton.nextElementSibling;
    if (answer.style.display === "block") {
      answer.style.display = "none";
    } else {
      answer.style.display = "block";
    }
  });
});

document.addEventListener("DOMContentLoaded", async function () {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("You must be logged in to access this page.");
        window.location.href = "/login"; // Redirect to login page
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/auth/validate", {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`, // Include token in header
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error("invalid token");
        }
	
	// show the client dashboard
	window.location.href = "/client";


    } catch (error) {
        console.error("Error fetching dashboard:", error);
        alert("Session expired. Please log in again.");
        localStorage.removeItem("token");
        window.location.href = "/login";
    }
});
