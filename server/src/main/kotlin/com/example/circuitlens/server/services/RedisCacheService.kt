package com.example.circuitlens.server.services

import org.slf4j.LoggerFactory
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.util.concurrent.ConcurrentHashMap

object RedisCacheService {
    private val logger = LoggerFactory.getLogger(RedisCacheService::class.java)
    private var jedisPool: JedisPool? = null
    private val inMemoryFallback = ConcurrentHashMap<String, String>()
    private var useFallback = false

    fun init() {
        val redisHost = System.getenv("REDIS_HOST") ?: "localhost"
        val redisPort = (System.getenv("REDIS_PORT") ?: "6379").toInt()

        try {
            logger.info("Connecting to Redis at $redisHost:$redisPort...")
            val poolConfig = JedisPoolConfig().apply {
                maxTotal = 10
                maxIdle = 5
                minIdle = 1
            }
            jedisPool = JedisPool(poolConfig, redisHost, redisPort, 2000)
            // Test connection
            jedisPool!!.resource.use { jedis ->
                jedis.ping()
            }
            logger.info("Successfully connected to Redis.")
        } catch (e: Exception) {
            logger.warn("Failed to connect to Redis ($redisHost:$redisPort). Falling back to in-memory caching. Error: ${e.message}")
            useFallback = true
        }
    }

    fun getCircuit(id: String): String? {
        if (useFallback) {
            return inMemoryFallback["circuit:$id"]
        }
        return try {
            jedisPool?.resource?.use { jedis ->
                jedis.get("circuit:$id")
            }
        } catch (e: Exception) {
            logger.warn("Redis read error, using in-memory cache fallback: ${e.message}")
            inMemoryFallback["circuit:$id"]
        }
    }

    fun setCircuit(id: String, payload: String) {
        if (useFallback) {
            inMemoryFallback["circuit:$id"] = payload
            return
        }
        try {
            jedisPool?.resource?.use { jedis ->
                jedis.set("circuit:$id", payload)
            }
        } catch (e: Exception) {
            logger.warn("Redis write error, using in-memory cache fallback: ${e.message}")
            inMemoryFallback["circuit:$id"] = payload
        }
    }
}
