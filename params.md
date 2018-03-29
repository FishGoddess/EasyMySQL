* url<br/>
传递给JDBC驱动的用于建立连接的url<br/>
*ex: jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8*

* username<br/>
传递给JDBC驱动用于建立连接的用户名<br/>
*ex: root*

* password<br/>
传递给JDBC驱动用于建立连接的密码<br/>
*ex: xxx*

* maxActive<br/>
最大活动连接数:连接池在同一时间能够分配的最大活动连接的数量，如果设置为非正数，则表示不受限制<br/>
*ex: 30*

* maxIdle<br/>
最大空闲连接数:连接池中容许保持最大空闲状态的最大链接数量，操作的空闲连接将被释放，如果设置为负数表示不受限制<br/>
*ex: 10*

* minIdle<br/>
最小空闲连接数:连接池中容许保持最小空闲状态的最大链接数量，操作的空闲连接将被释放，
如果设置为负数表示不受限制<br/>
*ex: 1*

* maxWait<br/>
最大等待时间：当没有可用连接时，连接池等待连接被归还的最大(以毫秒计数),超过时间则抛出异常，
如果设置为-1则表示无限等待<br/>
*ex: 1000*

* initialSize<br/>
初始连接:池启动时创建的连接数量<br/>
*ex: 1*

* logAbandoned<br/>
连接被泄露时是否打印<br/>
*ex: true*

* removeAbandoned<br/>
是否自动回收超时连接<br/>
*ex: true*

* removeAbandoned Timeout<br/>
超时时间(以秒数为单位)<br/>
*ex: 10*

* timeBetweenEvictionRunsMillis<br/>
在空闲连接回收器线程运行期间休眠的时间值,以毫秒为单位<br/>
*ex: 1000*

* numTestsPerEvictionRun<br/>
在每次空闲连接回收器线程(如果有)运行时检查的连接数量<br/>
*ex: 10*

* minEvictableIdleTimeMillis<br/>
连接在池中保持空闲而不被空闲连接回收器线程<br/>
*ex: 10000*