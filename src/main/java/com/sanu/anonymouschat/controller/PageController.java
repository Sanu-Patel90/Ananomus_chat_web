package com.sanu.anonymouschat.controller;

import com.sanu.anonymouschat.dto.FullSignupRequest; // Ensure this DTO exists in your project
import com.sanu.anonymouschat.service.UserService; // Ensure this service exists and has registerUser method
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller responsible for handling general application pages,
 * including registration, home, and error pages.
 */
@Controller
public class PageController {

    @Autowired
    private UserService userService; // Inject UserService to handle user registration logic

    // IMPORTANT: The @GetMapping("/auth/login") method has been REMOVED from this controller.
    // Login page handling is now exclusively in AuthController.

    /**
     * Displays the user registration form page.
     * This method maps to the URL /auth/register via a GET request.
     *
     * @param model Model to add attributes for the view, specifically an empty
     * FullSignupRequest object for form binding.
     * @return The logical view name "auth/register", which resolves to
     * src/main/resources/templates/auth/register.html.
     */
    @GetMapping("/auth/register")
    public String showRegisterPage(Model model) {
        // Add an empty DTO object for your form to bind to using th:object
        model.addAttribute("fullSignupRequest", new FullSignupRequest()); // Matches th:object="${fullSignupRequest}" in register.html
        return "auth/register"; // Refers to src/main/resources/templates/auth/register.html
    }

    /**
     * Handles the submission of the user registration form.
     * This method maps to the URL /auth/register via a POST request.
     *
     * @param request The FullSignupRequest DTO populated with form data.
     * @param redirectAttributes Used to add flash attributes (messages) that will be
     * available after a redirect (e.g., success or error messages).
     * @return A redirect URL, typically to the login page on success, or back to
     * the registration page on failure, with appropriate messages.
     */
    @PostMapping("/auth/register")
    public String registerUser(@ModelAttribute("fullSignupRequest") FullSignupRequest request,
                               RedirectAttributes redirectAttributes) {
        try {
            // Basic server-side validation for required fields.
            // This can be enhanced using JSR 303/380 annotations (@Valid) on the DTO.
            if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                    request.getPassword() == null || request.getPassword().isEmpty() ||
                    request.getGender() == null || request.getGender().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("registrationError", "Username, password, and gender are required.");
                return "redirect:/auth/register"; // Redirect back to registration with a specific error
            }

            // Call the UserService to perform the actual user registration logic.
            // This method in UserService should handle password encoding, role assignment,
            // and initial approval status based on your business rules.
            userService.registerUser(request);

            // On successful registration, add a flash attribute for a success message
            // and redirect the user to the login page.
            redirectAttributes.addFlashAttribute("registrationSuccess", true);
            return "redirect:/auth/login"; // Redirect to the login page (handled by AuthController)

        } catch (IllegalArgumentException e) {
            // Catch specific exceptions thrown by UserService (e.g., username already taken, email already registered).
            redirectAttributes.addFlashAttribute("registrationError", e.getMessage());
            return "redirect:/auth/register"; // Redirect back to registration with specific error message
        } catch (Exception e) {
            // Catch any other unexpected exceptions that might occur during registration.
            System.err.println("Error during registration: " + e.getMessage()); // Log the error for server-side debugging
            e.printStackTrace(); // Print full stack trace for detailed error analysis
            redirectAttributes.addFlashAttribute("registrationError", "An unexpected error occurred during registration. Please try again later.");
            return "redirect:/auth/register"; // Redirect back with a generic error message
        }
    }

    /**
     * Handles requests to display the access denied page.
     * This method maps to the URL /access-denied.
     *
     * @return The logical view name "access-denied", which resolves to
     * src/main/resources/templates/access-denied.html.
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; // Refers to src/main/resources/templates/access-denied.html
    }

    /**
     * Handles requests to the root URL of the application.
     *
     * @return A redirect to the login page. Spring Security will then
     * automatically redirect to a dashboard (like /home or /user/online-page)
     * if the user is already authenticated.
     */
    @GetMapping("/")
    public String rootPage() {
        return "redirect:/auth/login"; // Redirects to the login page
    }

    /**
     * Handles requests to the general home page after successful authentication.
     * This method maps to the URL /home.
     *
     * @return The logical view name "home", which resolves to
     * src/main/resources/templates/home.html.
     */
    /*@GetMapping("/home")
    public String homePage() {
        return "home"; // Refers to src/main/resources/templates/home.html
    }*/
}