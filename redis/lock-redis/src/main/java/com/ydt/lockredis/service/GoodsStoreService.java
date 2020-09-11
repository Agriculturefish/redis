package com.ydt.lockredis.service;

import com.ydt.lockredis.domain.GoodsStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsStoreService{
	String updateGoodsStore(String code,int count);

	GoodsStore getGoodsStore(String code);
}
