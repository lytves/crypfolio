package tk.crypfolio.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.business.UserService;
import tk.crypfolio.model.UserEntity;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "ResetPasswordFilter", urlPatterns = {"/reset-password/*"})
public class ResetPasswordFilter implements Filter {

    private static final Logger LOGGER = LogManager.getLogger(ResetPasswordFilter.class);

    private FilterConfig filterConfig;

    // stateless business
    @Inject
    private UserService userService;

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

            String resetPasswordCode = req.getParameter("code");

            if (resetPasswordCode != null) {

                UserEntity user = userService.searchUserResetPasswordCodeDB(resetPasswordCode);

                if (user != null) {

                    /*
                     * case: there is a user in BD and the reset code is valid
                     * */
                    filterConfig.getServletContext().getRequestDispatcher("/forgot-password").forward(req, resp);

                } else {

                    /*
                     * some error, like the link has been already used, or code is incorrect or is already expired
                     * */
                    req.setAttribute("errorText", "Reset password error. " +
                            "The code is invalid or has been already used or the link is expired. " +
                            "If you want reset your account password, " +
                            "try to request new reset password link by email again.");

                    filterConfig.getServletContext().getRequestDispatcher("/error").forward(req, resp);
                }

            } else {

                filterChain.doFilter(servletRequest, servletResponse);
            }

        } catch (Exception e) {

            LOGGER.warn(e.getMessage());
        }
    }

    @Override
    public void destroy() {
    }
}