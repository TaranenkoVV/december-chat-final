package ru.flamexander.december.chat.server.repository;

import java.util.List;
import ru.flamexander.december.chat.server.model.User;

public interface UsersRepository {


    /**
     * Создание записи в БД.
     *
     * @param user - заполненный объект
     * @return
     */
    User create(User user);

    /**
     * Получение записи по login из БД.
     *
     * @param login идентификатор записи
     * @return запись
     */
    User selectById(String login);


    /**
     * Получение всех записей из БД.
     *
     * @return записи
     */
    List<User> selectAll();


    /**
     * Обновление записи в БД.
     *
     * @param user изменяемая запись
     * @return количество обновленных записей
     */
    int update(User user);


    /**
     * Удаление указанных записей по login.
     *
     * @param idList список идентификаторов записей
     * @return количество удаленных записей
     */
    int remove(List<String> loginList);

}
