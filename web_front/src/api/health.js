import request from '@/utils/request'

/**
 * 健康检查
 */
export function getHealth() {
  return request.get('/health')
}

/**
 * Redis 连通检查
 */
export function getRedisHealth() {
  return request.get('/health/redis')
}
