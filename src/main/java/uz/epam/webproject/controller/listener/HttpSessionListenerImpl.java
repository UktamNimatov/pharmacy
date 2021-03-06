package uz.epam.webproject.controller.listener;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uz.epam.webproject.controller.command.ParameterName;
import uz.epam.webproject.entity.user.UserRole;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ResourceBundle;

@WebListener
public class HttpSessionListenerImpl implements HttpSessionListener {
    private static final Logger logger = LogManager.getLogger();
    private static final String DEFAULT_LOCALE = "en_US";
    private static final String DEFAULT_LANGUAGE = "EN";
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(ParameterName.PAGES_PATH);

    public HttpSessionListenerImpl() {
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
//        HttpSessionListener.super.sessionCreated(se);
        HttpSession httpSession = se.getSession();
        httpSession.setAttribute(ParameterName.CURRENT_PAGE, resourceBundle.getString(ParameterName.INDEX_PAGE));
        httpSession.setAttribute(ParameterName.LOCALE, DEFAULT_LOCALE);
        httpSession.setAttribute(ParameterName.LANGUAGE, DEFAULT_LANGUAGE);
        httpSession.setAttribute(ParameterName.ROLE, UserRole.GUEST.getRoleName());
        logger.log(Level.INFO, "<><><> Session created: " + se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
//        HttpSessionListener.super.sessionDestroyed(se);
        logger.log(Level.INFO, "<><><> Session destroyed: " + se.getSession().getId());
    }
}
