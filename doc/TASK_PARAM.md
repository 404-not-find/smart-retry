## 重试方法参数说明
有两种方法可以实现定时重试
* 需要重试的逻辑写在带有@RetryFunction注解的方法里面，这里暂且叫handle方法
* 实现RetryHandler接口，把逻辑写在handle方法里面


其中handle方法必须要有参数，且只能有一个参数。这个参数的类型不能是任何类型，需要满足下面条件
* 不能是Object
* 不能是Collection、Map等集合类型
* 其他带泛型的对象
* 不能是无法使用JSON进行序列化和反序列化的对象

建议这个handle方法的参数是一个java bean（javabean里面的属性类型可以是Collection、Map等集合类型）

handle方法的参数序列化和反序列化默认使用的是jackson2，如果需要使用fastjson或者gson可以在pom.xml依赖中排除retry-serializer-jackson2依赖，再加上retry-serializer-fastjson或者retry-serializer-gson的依赖。
如果这3种序列化方式不满足自己的需要，可以自己扩展com.github.smartretry.core.RetrySerializer接口，然后托管到Spring容器


handle方法的参数需要特别注意能否序列化和反序列化（能否正常的序列化和反序列化是任务能否重试的关键），比如说下面这个对象
    
    @Setter
    @Getter
    public class Order {
    
        private Integer orderId;
    
        private Integer userId;
    
        private LocalDateTime createTime;
    }

在使用jackson2序列化的时候，序列化的结果是

    {
    	"orderId": 1,
    	"userId": 1001,
    	"createTime": {
    		"month": "MARCH",
    		"year": 2020,
    		"dayOfMonth": 18,
    		"hour": 19,
    		"minute": 24,
    		"monthValue": 3,
    		"nano": 664000000,
    		"second": 26,
    		"dayOfWeek": "WEDNESDAY",
    		"dayOfYear": 78,
    		"chronology": {
    			"id": "ISO",
    			"calendarType": "iso8601"
    		}
    	}
    }

显然，这个createTime字段的序列化结果不是我们想要的（无法在任务重试的时候，反序列化回原来的对象），解决方法有很多，自定义jackson的序列化和反序列化方法、使用String或者long类型或者java.util.Date类型