package uz.epam.webproject.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uz.epam.webproject.dao.UserDao;
import uz.epam.webproject.dao.exception.DaoException;
import uz.epam.webproject.dao.mapper.ColumnName;
import uz.epam.webproject.dao.mapper.impl.UserMapper;
import uz.epam.webproject.entity.user.User;
import uz.epam.webproject.entity.user.UserRole;
import uz.epam.webproject.pool.ConnectionPool;
import uz.epam.webproject.service.impl.UserServiceImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao<User> {
    private static final Logger logger = LogManager.getLogger();
    private UserMapper userMapper = new UserMapper();

    private static final String ADD_USER = "INSERT INTO user (login, password, first_name, last_name, email, role) values (?, ?, ?, ?, ?, ?)";
    private static final String EMAIL_FROM_LOGIN = "SELECT user.password FROM user WHERE user.login = ?";
//    private static final String SELECT_ALL_USERS = "SELECT * FROM user ";
    private static final String SELECT_ALL_USERS = "SELECT user.id, user.login, user.password, user.first_name, user.last_name, user.email, user.role FROM user";
    private static final String SELECT_BY_LOGIN = "SELECT user.id, user.login, user.password, user.first_name, user.last_name, user.email, user.role FROM user WHERE user.login = ?";
    private static final String FIND_USER_ROLE_BY_LOGIN = "SELECT user.role FROM user WHERE user.login = ?";

    private static UserDaoImpl instance;

    public static UserDaoImpl getInstance() {
        if (instance == null){
            return instance = new UserDaoImpl();
        }
        return instance;
    }

    @Override
    public boolean registerUser(User user) throws DaoException{
        boolean toReturn = false;
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(ADD_USER)){
            if (user != null) {
                preparedStatement.setString(1, user.getLogin());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getFirstName());
                preparedStatement.setString(4, user.getLastName());
                preparedStatement.setString(5, user.getEmail());
                preparedStatement.setString(6, user.getRole().getRoleName().toLowerCase());
                toReturn = preparedStatement.executeUpdate() != 0;
            }
        } catch (SQLException sqlException) {
            logger.error("error in connecting the database", sqlException);
            throw new DaoException(sqlException);
        }
        return toReturn;
    }

    @Override
    public boolean authenticate(String login, String password) throws DaoException {
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EMAIL_FROM_LOGIN)){
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            String passwordFromDb;
            if (resultSet.next()){
                passwordFromDb = resultSet.getString(1);
                return passwordFromDb.equals(password);
            }
        } catch (SQLException sqlException) {
            logger.error("error in connecting the database", sqlException);
            throw new DaoException(sqlException);
        }
        return false;
    }

    @Override
    public List<User> findAll() throws DaoException {
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_USERS)){
            ResultSet resultSet = statement.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()){
                Optional<User> optionalUser = userMapper.map(resultSet);
                optionalUser.ifPresent(users::add);
            }
            return users;
        } catch (SQLException sqlException) {
            logger.error("error in connecting the database", sqlException);
            throw new DaoException(sqlException);
        }
    }

    @Override
    public Optional<User> findByLogin(String login) throws DaoException {
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_LOGIN)){
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return userMapper.map(resultSet);
            }
        } catch (SQLException sqlException) {
            logger.error("error in connecting the database", sqlException);
            throw new DaoException(sqlException);
        }
        return Optional.empty();
    }

    @Override
    public UserRole findUserRole(String login) throws DaoException {
            UserRole userRole = null;
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_ROLE_BY_LOGIN)){
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                userRole = UserRole.valueOf(resultSet.getString(ColumnName.ROLE).toUpperCase());
            }
        } catch (SQLException sqlException) {
            logger.error("error in connecting the database", sqlException);
            throw new DaoException(sqlException);
        }
        return userRole;
    }

    @Override
    public boolean checkLogin(String login) throws DaoException {
        return false;
    }

    @Override
    public boolean updatePassword(String login, String newPassword) throws DaoException {
        return false;
    }

}
