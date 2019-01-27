package tk.crypfolio.filter;

import tk.crypfolio.view.ActiveUser;
import tk.crypfolio.model.UserEntity;
import tk.crypfolio.business.UserService;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebFilter(filterName = "VerifyEmailFilter", urlPatterns = {"/verify-email/*"})
public class VerifyEmailFilter implements Filter {

    private static final Logger logger = Logger.getLogger(VerifyEmailFilter.class.getName());

    private FilterConfig filterConfig;

    // stateless business
    @Inject
    private UserService userService;

    // session managed bean
    @Inject
    private ActiveUser activeUser;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        try {

            HttpServletRequest req = (HttpServletRequest) servletRequest;
            HttpServletResponse resp = (HttpServletResponse) servletResponse;

            String emailVerifCode = req.getParameter("code");

            if (emailVerifCode != null) {

                UserEntity user = userService.doConfirmEmailDB(emailVerifCode);

                if (user != null) {

                    /*
                     * the user's email had been confirmed successfully
                     * */
                    activeUser.setUser(user);

                    // TODO make some variable in activeUser with text to show a modal popup
                    //  on /user page to inform when email verification is done

                    resp.sendRedirect(req.getContextPath() + "/user");

                } else {

                    /*
                     * some error, like the email has been already confirmed, or code is incorrect
                     * or expired
                     * */
                    req.setAttribute("errorText", "Email address verification error. " +
                            "The code is invalid or has been already confirmed or the link is expired. " +
                            "Try to request new email address confirmation link by email again.");

                    filterConfig.getServletContext().getRequestDispatcher("/error").forward(req, resp);
                }

            } else {

                filterChain.doFilter(servletRequest, servletResponse);
            }

        } catch (Exception e) {

            logger.log(Level.WARNING, e.getMessage());
        }
    }

    @Override
    public void destroy() {

    }
}