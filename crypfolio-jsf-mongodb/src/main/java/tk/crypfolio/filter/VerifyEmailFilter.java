package tk.crypfolio.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.view.ActiveUser;
import tk.crypfolio.model.UserEntity;
import tk.crypfolio.business.UserService;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "VerifyEmailFilter", urlPatterns = {"/verify-email/*"})
public class VerifyEmailFilter implements Filter {

    private static final Logger LOGGER = LogManager.getLogger(VerifyEmailFilter.class);

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
                     * some error, like the email has been already confirmed, or code is incorrect or is already expired
                     * */
                    req.setAttribute("errorText", "Email address verification error. " +
                            "The code is invalid or has been already confirmed or the link is expired. " +
                            "Try to request new email address confirmation link by email again.");

                    filterConfig.getServletContext().getRequestDispatcher("/error").forward(req, resp);
                }

            } else {

                filterChain.doFilter(servletRequest, servletResponse);
            }

        } catch (Exception ex) {

            LOGGER.warn(ex.getMessage());
        }
    }

    @Override
    public void destroy() {

    }
}