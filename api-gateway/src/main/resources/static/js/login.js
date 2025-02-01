function toggleForm(formType) {
  const loginForm = document.getElementById('login-form');
  const signupForm = document.getElementById('signup-form');

  if (formType === 'signup') {
    loginForm.style.display = 'none';
    signupForm.style.display = 'block';
  } else {
    loginForm.style.display = 'block';
    signupForm.style.display = 'none';
  }
}

document.addEventListener("DOMContentLoaded", function () {


	document.getElementById("login-form").addEventListener("submit", async function(event) {
    
		event.preventDefault(); // Prevent form submission

   
		// Get input values
		const email = document.getElementById("loginUsername").value;
		const password = document.getElementById("loginPassword").value;

		try {
			const response = await fetch("http://localhost:8080/api/auth/authenticate", {
        		    method: "POST",
        		    headers: { "Content-Type": "application/json" },
        		    body: JSON.stringify({ email, password })
        		});

        		const data = await response.json();

        		if (response.ok) {
        		    localStorage.setItem("token", data.token); // Store token for authentication
        		    localStorage.setItem("role", data.role);   // Store user role

        		    alert("Login successful!");

        		    // Redirect based on role
        		    if (data.role === "ADMIN") {
        		        dashboardUrl = "/admin";  
        		    } else if (data.role === "CLIENT") {
        		        dashboardUrl = "/client";  
        		    } else {
        		        dashboardUrl = "/user";  
			    }
				// Redirect to dashboard with token
				window.location.href = `$dashboardUrl?token=${data.token}`;
        		} else {
        		    alert("Login failed: " + (data.message || "Invalid credentials"));
        		}
		} catch (error) {
			console.error("Error logging in:", error);
			alert("An error occurred. Please try again.");
		}
	});
    // Handle Signup Form Submission
    document.getElementById("signup-form").addEventListener("submit", async function (event) {
        event.preventDefault();

        const username = document.getElementById("signupName").value;
        const email = document.getElementById("signupEmail").value;
        const role = document.getElementById("role").value;
        const password = document.getElementById("signupPassword").value;
        const confirmPassword = document.getElementById("signupConfirmPassword").value;

        if (password !== confirmPassword) {
            alert("Passwords do not match!");
            return;
        }

        const response = await fetch("http://localhost:8080/api/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ username, email, role, password }),
        });

        const result = await response.json();
        if (response.ok) {
            alert("Signup Successful!");
            // Redirect to login or dashboard
            window.location.href = "/login";
        } else {
            alert(result.message || "Signup Failed");
        }
    });
});
