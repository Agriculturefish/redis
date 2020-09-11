package com.ydt.lockredis.dao;

import com.ydt.lockredis.domain.GoodsStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;


@Component
public interface GoodsStoreDao extends JpaRepository<GoodsStore, String> {
}
