package org.seckill.dao.cache;

import org.seckill.entity.Seckill;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
	
	private final JedisPool jedisPool;
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
	
	public RedisDao(String ip, int port) {
		jedisPool = new JedisPool(ip, port);
	}
	
	public Seckill getSeckill(long seckillId) {
		
		try{
			Jedis jedis = jedisPool.getResource();
			
			try{
				String key = "seckill:" + seckillId;
				byte[] bytes = jedis.get(key.getBytes());
				if(bytes != null) {
					Seckill seckill = schema.newMessage();//生成空对象
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					
					return seckill;
				}
			}finally{
				jedis.close();
			}	
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String putSeckill(Seckill seckill){
		
		try{
			Jedis jedis = jedisPool.getResource();
			
			try{
				String key = "seckill:" + seckill.getSeckillId();
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, 
						LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				int timeout = 60 * 60;//超时缓存 1小时
				String result = jedis.setex(key.getBytes(), timeout, bytes);
				return result;
			}finally{
				jedis.close();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}