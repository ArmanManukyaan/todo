package com.example.todorest.component;

import com.example.todorest.dto.UserSearchDto;
import com.example.todorest.entity.QUser;
import com.example.todorest.entity.User;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class UserFilterManager {
    private final QUser qUser = QUser.user;
    @PersistenceContext
    private EntityManager entityManager;

    public List<User> searchUserByFilter(int size, int page, UserSearchDto searchDto) {
        var query = new JPAQuery<User>(entityManager);
        JPAQuery<User> from = query.from(qUser);
        filterName(from, searchDto.getName());
        filterSurName(from, searchDto.getSurname());
        filterEmail(from, searchDto.getEmail());
        getPagination(from, page, size);
        getSorting(from, searchDto.getSortBy(), searchDto.getSortDirection());
        return from.fetch();
    }

    public void filterName(JPAQuery<User> from, String name) {
        if (name != null && !name.isEmpty()) {
            from.where(qUser.name.eq(name));
        }
    }

    public void filterSurName(JPAQuery<User> from, String surname) {
        if (surname != null && !surname.isEmpty()) {
            from.where(qUser.surname.contains(surname));
        }
    }

    public void filterEmail(JPAQuery<User> from, String email) {
        if (email != null && !email.isEmpty()) {
            from.where(qUser.email.contains(email));
        }
    }

    private void getPagination(JPAQuery<User> from, int page, int size) {
        if (page > 0) {
            from.offset((long) page * size);
        }
        from.limit(size);
    }

    private void getSorting(JPAQuery<User> from, String sortBy, String sortDirection) {
        if (sortBy != null && !sortBy.isEmpty()) {
            PathBuilder<Object> orderByExpression = new PathBuilder<Object>(User.class, sortBy);
            from.orderBy(new OrderSpecifier("asc".equalsIgnoreCase(sortDirection)
                    ? Order.ASC : Order.DESC, orderByExpression));
        }
    }
}

