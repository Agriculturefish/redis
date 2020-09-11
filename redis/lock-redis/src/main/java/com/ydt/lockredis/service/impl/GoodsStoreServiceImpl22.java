package com.ydt.lockredis.service.impl;

import com.ydt.lockredis.dao.GoodsStoreDao;
import com.ydt.lockredis.domain.GoodsStore;
import com.ydt.lockredis.service.GoodsStoreService;
import com.ydt.lockredis.util.RedissonLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 库存管理服务
 *
 */
@Service("goodsStoreServiceImpl22")
public class GoodsStoreServiceImpl22 implements GoodsStoreService {


    @Autowired
    private RedissonLock redissonLock;

    @Autowired
    private GoodsStoreDao goodsStoreDao;

    /**
     * 超时时间 5s
     */
    private static final int TIMEOUT = 5*1000;

    /**
     * 根据产品编号更新库存
     * @param code
     * @return
     */
    @Override
    public String updateGoodsStore(String code,int count) {
        //上锁
        long time = System.currentTimeMillis() + TIMEOUT;
        boolean result = redissonLock.lock(code);
        if (result) {
            System.out.println("获得锁的时间戳：" + String.valueOf(time));
            System.out.println("没有线程在操作" + Thread.currentThread().getName() + "正常执行");
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
                redissonLock.release(code);
                return "恭喜您，购买成功！";
            }
            else {
                redissonLock.release(code);
                return "获取库存失败。";
            }
        }else{
            return "排队人数太多，请稍后再试.";
        }
    }

    /**
     * 获取库存对象
     * @param code
     * @return
     */
    @Override
    public GoodsStore getGoodsStore(String code){
        Optional<GoodsStore> optional = goodsStoreDao.findById(code);
        return optional.get();
    }
}
