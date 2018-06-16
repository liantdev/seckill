package org.seckill.service.imp;

import java.util.Date;
import java.util.List;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatSeckillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
public class SeckillServiceImp implements SeckillService {
	
	private String slat = "$%^&Hjujn6878$&9KHN98%UN67njGHy";
	
	@Autowired
	private SeckillDao seckillDao;
	
	@Autowired
	private SuccessKilledDao successKilledDao;
	
	@Autowired
	private RedisDao redisDao;
	
	
	public List<Seckill> getSeckillList() {

		return seckillDao.queryAllSeckill(0, 4);
	}

	public Seckill getSeckillById(long seckillId) {

		return seckillDao.querySeckillById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		//优化点：缓存优化
		//1.访问redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null) {
			//2.如果redis中不存在，从数据库中查找
			seckill = seckillDao.querySeckillById(seckillId);
			if(seckill == null) {
				return new Exposer(false, seckillId);
			}else {
				//3.将数据存放到redis中
				redisDao.putSeckill(seckill);
			}
		}
		
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		//系统当前时间
		Date nowTime = new Date();
		
		if(nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}
	
	private String getMD5(long seckillId) {
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
	
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) {
		
		if(md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		
		Date killTime = new Date();
		try {
			//插入购买行为
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			if(insertCount <=0 ) {
				throw new RepeatSeckillException("seckill repeated");
			} else {
				//减库存
				int updateCount = seckillDao.reduceNum(seckillId, killTime);
				if(updateCount <= 0) {
					throw new SeckillCloseException("seckill is closed");
				}
				
				SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
			}
		} catch (SeckillCloseException e1) {
			throw e1;
		} catch (RepeatSeckillException e2) {
			throw e2;
		} catch (Exception e) {
			throw new SeckillException("seckill inner error:" + e.getMessage());
		}
	}

}
