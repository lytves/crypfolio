package tk.crypfolio.filter;

import tk.crypfolio.controller.ActiveUser;
import tk.crypfolio.domain.UserEntity;
import tk.crypfolio.ejb.UserService;

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

    // stateless ejb
    @Inject
    protected UserService userService;

    // session managed bean
    @Inject
    private ActiveUser activeUser;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        try {

            HttpServletRequest req = (HttpServletRequest) servletRequest;
            HttpServletResponse resp = (HttpServletResponse) servletResponse;

            String emailVerifCode = req.getParameter("code");

            if (emailVerifCode != null) {

                UserEntity user = userService.doConfirmEmail(emailVerifCode);

                if (user != null) {

                    /*
                     * the user's email had been confirmed successfully
                     * */
                    activeUser.setUser(user);

                    resp.sendRedirect(req.getContextPath() + "/user");

                } else {

                    /*
                     * some error, like the email has already been confirmed
                     * */
                    resp.sendRedirect(req.getContextPath() + "/error");

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
