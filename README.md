# Pinyin
基于qq的城市数据，用JAVA工程弄成自己需要的json格式，中国城市与世界城市（港澳台可选），多音字处理
# Lib
这个是用AndroidStudio弄的JAVA工程，所以不必关注app工程，东西都在lib里，直接运行lib里的main文件就行
# Resource
+ LocList.xml qq的城市数据
+ city.json city_origin.json xml转json后修正JsonObject为JsonArray的数据
+ city_with_hk.json 国内城市带港澳台
+ city_without_hk.json 国内城市不带港澳台
+ world_with_hk.json 世界城市带港澳台
+ world_without_hk.json 世界城市不带港澳台
+ city_change.txt 转化后的数据json（需要的）
+ duoyinzi_dic.txt 多音字词典，可在此添加多音字
#注意事项
关于路径，用的是本地路径，需要手动改成自己本机的路径。（getClassLoader().getResource或者getResource找不到资源路径，
只能用本地路径，有能解决的也可联系我）Change和PinyinUtil只有这两个文件里有路径，其他不用动
