package com.ydt.lockredis.service.impl;

import com.ydt.lockredis.dao.GoodsStoreDao;
import com.ydt.lockredis.domain.GoodsStore;
import com.ydt.lockredis.service.GoodsStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 库存管理服务
 */
@Service("goodsStoreServiceImpl0")
public class GoodsStoreServiceImpl0 implements GoodsStoreService {


    @Autowired
    private GoodsStoreDao goodsStoreDao;


    /**
     * 根据产品编号更新库存（啥控制也不加）
     *
     * @param code
     * @return
     */   @Override
    public String updateGoodsStore(String code, int count) {
        GoodsStore goodsStore = getGoodsStore(code);
        if (goodsStore != null) {
            if (goodsStore.getStore() <= 0) {
                return "对不起，卖完了，库存为：" + goodsStore.getStore();
            }
            if (goodsStore.getStore() < count) {
                return "对不起，库存不足，库存为：" + goodsStore.getStore() + " 您的购买数量为：" + count;
            }
            System.out.println("剩余库存：" + goodsStore.getStore());
            System.out.println("扣除库存：" + count);
            goodsStore.setStore(goodsStore.getStore() - count);
            goodsStoreDao.save(goodsStore);
            try {
                //为了更好的测试多线程同时进行库存扣减，在进行数据更新之后先等1秒，让多个线程同时竞争资源
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "恭喜您，购买成功！";
        } else {
            return "获取库存失败。";
        }
    }

    /**
     * 获取库存对象
     *
     * @param code
     * @return
     */
    @Override
    public GoodsStore getGoodsStore(String code) {
        Optional<GoodsStore> optional = goodsStoreDao.findById(code);
        return optional.get();
    }
}
