package com.personal.smartbudgetcraft.global.config.redis.dao;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * Refresh Token 을 저장하는 Redis DAO
 * Key = 회원의 ID
 * data = Refresh Token
 */
@Component
@RequiredArgsConstructor
public class RedisRepository {

  private final RedisTemplate<String, String> redisTemplate;

  /**
   * 만료가 포함되지 않은 값 넣기
   *
   * @param key  key
   * @param data 값
   */
  public void setValues(String key, String data) {
    ValueOperations<String, String> values = redisTemplate.opsForValue();
    values.set(key, data);
  }

  /**
   * 만료가 포함된 값 넣기
   *
   * @param key      key
   * @param data     값
   * @param duration 만료일
   */
  public void setValues(String key, String data, Duration duration) {
    ValueOperations<String, String> values = redisTemplate.opsForValue();
    values.set(key, data, duration);
  }

  /**
   * 데이터 가져오기
   *
   * @param key key
   * @return key 에 해당하는 값
   */
  public String getValues(String key) {
    ValueOperations<String, String> values = redisTemplate.opsForValue();
    return values.get(key);
  }

  /**
   * 데이터 삭제하기
   *
   * @param key key
   */
  public void deleteValues(String key) {
    redisTemplate.delete(key);
  }
}