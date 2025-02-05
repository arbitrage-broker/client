package com.arbitragebroker.client.controller.impl;


import com.arbitragebroker.client.controller.BaseRestController;
import com.arbitragebroker.client.exception.BadRequestException;
import com.arbitragebroker.client.model.BaseModel;
import com.arbitragebroker.client.model.Select2Model;
import com.arbitragebroker.client.service.BaseService;
import com.arbitragebroker.client.util.CustomObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

import static com.arbitragebroker.client.util.StringUtils.generateFilterKey;
import static com.arbitragebroker.client.util.StringUtils.generateIdKey;

@Slf4j
@PropertySource("classpath:i18n/messages.properties")
public abstract class BaseRestControllerImpl<F,M extends BaseModel<ID>, ID extends Serializable> implements BaseRestController<F, M, ID> {
    protected BaseService<F, M, ID> service;
    protected CustomObjectMapper objectMapper;

    public BaseRestControllerImpl(BaseService<F, M, ID> service) {

        this.service = service;
        this.objectMapper = new CustomObjectMapper();
    }
    protected String getClassName() {
        var className =  service.getClass().getName().replace("com.arbitragebroker.client.service.impl.","");
        var index = className.indexOf("ServiceImpl");
        return className.substring(0, index);
    }

    @Override
    public ResponseEntity<M> findById(ID id) {
        log.debug("call BaseRestImpl.findById {}, for class {}", id, service.getClass().getName());
        return ResponseEntity.ok(service.findById(id, generateIdKey(getClassName(),id)));
    }

    @SneakyThrows
    @Override
    public ResponseEntity<Page<M>> findAll(F filter, Pageable pageable) {
        log.debug("call BaseRestImpl.findAll {}, for class {}", filter, service.getClass().getName());
        return ResponseEntity.ok(service.findAll(filter, pageable,generateFilterKey(getClassName(),"findAll",filter, pageable)));
    }

//    @SneakyThrows
//    @Override
//    public ResponseEntity<PageDto<M>> findAllTable(F filter, Pageable pageable) {
//        log.debug("call BaseRestImpl.findAllTable {}, for class {}", filter, service.getClass().getName());
//        return ResponseEntity.ok(service.findAllTable(filter, pageable,generateFilterKey(getClassName(),"findAllTable",filter,pageable)));
//    }

    @SneakyThrows
    @Override
    public Page<Select2Model> findAllSelect(F filter, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "id");
        return service.findAllSelect(filter, pageable,generateFilterKey(getClassName(),"findAllSelect",filter,pageable));
    }

    @SneakyThrows
    @Override
    public ResponseEntity<Long> countAll(F filter) {
        log.debug("call BaseRestImpl.countAll {}, for class {}", filter, service.getClass().getName());
        return ResponseEntity.ok(service.countAll(filter,generateFilterKey(getClassName(),"findAll",filter,null)));
    }

    @SneakyThrows
    @Override
    public ResponseEntity<Boolean> exists(F filter) {
        log.debug("call BaseRestImpl.exists {}, for class {}", filter, service.getClass().getName());
        return ResponseEntity.ok(service.exists(filter,generateFilterKey(getClassName(),"findAll",filter,null)));
    }

    @Override
    public ResponseEntity<Void> deleteById(ID id) {
        log.debug("call BaseRestImpl.deleteById {}, for class {}", id, service.getClass().getName());
        service.deleteById(id, String.format("%s:*", getClassName()));
        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<M> create(M model) {
        log.debug("call BaseRestImpl.save for {}", model);
        if (model.getId() != null) {
            throw new BadRequestException("The id must not be provided when creating a new record");
        }
        return ResponseEntity.ok(service.create(model, String.format("%s:*", getClassName())));
    }

    @Override
    public ResponseEntity<M> update(ID id, M model) {
        log.debug("call BaseRestImpl.update for {}", model);
        model.setId(id);
        return ResponseEntity.ok(service.update(model, generateIdKey(getClassName(),model.getId()), String.format("%s:*", getClassName())));
    }
}
