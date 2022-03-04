package org.pcloud.admin.service;

import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.repository.AdminRepository;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SpyAdminRepository implements AdminRepository {
    public Admin save_argumentAdmin;
    public Pageable findAll_argumentRequest;
    public List<Admin> findAll_returnValue = new ArrayList<>();
    public String existsById_argumentId;
    public boolean existsById_returnValue;
    public String findById_argumentId;
    public Optional<Admin> findById_returnValue;

    @Override
    public List<Admin> findAll() {
        return null;
    }

    @Override
    public List<Admin> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Admin> findAll(Pageable pageable) {
        findAll_argumentRequest = pageable;
        return new PageImpl<Admin>(findAll_returnValue, pageable, findAll_returnValue.size());
    }

    @Override
    public List<Admin> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public long count() {
        return findAll_returnValue.size();
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(Admin entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends Admin> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Admin> S save(S entity) {
        save_argumentAdmin = entity;
        return entity;
    }

    @Override
    public <S extends Admin> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Admin> findById(String s) {
        findById_argumentId = s;
        return findById_returnValue;
    }

    @Override
    public boolean existsById(String s) {
        existsById_argumentId = s;
        return existsById_returnValue;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Admin> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Admin> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Admin> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Admin getOne(String s) {
        return null;
    }

    @Override
    public Admin getById(String s) {
        return null;
    }

    @Override
    public <S extends Admin> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Admin> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Admin> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Admin> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Admin> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Admin> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Admin, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}
