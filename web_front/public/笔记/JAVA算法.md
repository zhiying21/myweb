JAVA算法

```
map.containsKey(complement)是用于判断map是否包含complement这个元素
```

```
map.put(10, "Apple");
System.out.println(map.get(10));     // "Apple"
用于键值对的输入输出
当我们想要知道某个值是否出现过,可以将键值对反过来去找值,前提是只有一个有效答案
```

降低时间复杂度方法一:用图记录出现过的元素

```
char[] chars = s.toCharArray();
字符串转成字符数组
Arrays.sort(chars);
将数组按字典排序
String key = new String(chars);
字符数组转化为字符串
```

```
map.putIfAbsent(key, new ArrayList<>());
如果 map 中还没有这个 key，就创建一个新的空列表作为 value
这样通过key键可以存放多个值
map.get(key).add(s);#ArrayList用add添加元素,map用put,这里键对应的值是一个数组
map.value可以获取到这个组的全部值
```

```
如果要将某一个元素放在最后面,可以用双循环,在外层循环下如nums[i]=nums[nums.length-1],再用一个循环将i与length-1之间的元素往前挪一位,
这时候超时可以用快慢墙的方式,把不符合的快的,赋值给慢的slow++;最后统一将大于slow之后的值就是符合的值
```

```
盛水题双循环会超时,可以使用左右端靠近的方式,循环条件为左小于右,Math.min(a,b),Math.max(a,b)可以返回对应的数
```

```
 Set<Integer> num_set = new HashSet<Integer>();
        for (int num : nums) {
            num_set.add(num);
        }
这个写法可以让数组去重Set自带
```

```
序列长可以使用满足条件才开启内层循环的方式降低时间复杂度;所以类似的题目找开启内循环的起点,例如用上一个数是否存在来判断这个数是不是起点  .contains()
```

