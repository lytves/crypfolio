package tk.crypfolio.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebFilter(filterName = "AuthFilter", urlPatterns = { "/*"})
public class AuthFilter implements Filter {

    private static final Logger logger = Logger.getLogger(AuthFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        try {

            HttpServletRequest req = (HttpServletRequest) servletRequest;
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            HttpSession session = req.getSession(false);

            String pageOrigin = req.getServletPath();

            // admin handler
            if (pageOrigin.startsWith("/management")
                    && session != null
                    && session.getAttribute("isAdmin") != null
                    && session.getAttribute("userEmailSession") != null){

                filterChain.doFilter(servletRequest, servletResponse);
                logger.info("\nsuccessfully passed by 'admin' AuthFilter.doFilter\n");
            }

            // authorizing user handler
            else if (session != null
                    && session.getAttribute("userEmailSession") != null){

                if (pageOrigin.startsWith("/signup")
                        || pageOrigin.startsWith("/reset_pass")
                        || pageOrigin.startsWith("/login")){

                    resp.sendRedirect(req.getContextPath() + "/");
                    logger.info("\nredirected to '/' by AuthFilter.doFilter\n");
                }

                filterChain.doFilter(servletRequest, servletResponse);
                logger.info("\nsuccessfully passed by 'user' AuthFilter.doFilter\n");
            }

            // not authorizing user handler
            else if (session == null && !pageOrigin.startsWith("/login")){

                resp.sendRedirect(req.getContextPath() + "/login");
                logger.info("\nredirected to '/login' by AuthFilter.doFilter\n");
            }

            // normal response handler
            else {

                filterChain.doFilter(servletRequest, servletResponse);
                logger.info("\nsuccessfully passed by 'default' AuthFilter.doFilter\n");
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    @Override
    public void destroy() {

    }
}
