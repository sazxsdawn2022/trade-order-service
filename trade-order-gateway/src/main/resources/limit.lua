-- bucket_id: 漏桶的 ID
-- capacity: 漏桶的容量
-- rate: 漏桶的速率（单位：个/秒）
-- timestamp: 当前时间戳（单位：毫秒）
local bucket_id = KEYS[1]
local capacity = tonumber(ARGV[1])
local rate = tonumber(ARGV[2])
local timestamp = tonumber(ARGV[3])

-- 获取漏桶的状态
local state = redis.call('hmget', bucket_id, 'water', 'timestamp')
local water = tonumber(state[1] or '0')
local last_timestamp = tonumber(state[2] or '0')

-- 计算漏桶的流出量
local outflow = math.max(0, (timestamp - last_timestamp) / 1000 * rate)
water = math.max(0, water - outflow)

-- 判断漏桶是否溢出
if water + 1 <= capacity then
    water = water + 1
    redis.call('hmset', bucket_id, 'water', water, 'timestamp', timestamp)
    return 1
else
    return 0
end