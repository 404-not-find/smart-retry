package com.github.smartretry.samples.order;

import com.github.smartretry.core.RetryFunction;
import com.github.smartretry.samples.order.entity.Order;
import com.github.smartretry.samples.order.model.CreateOrderReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @author yuni[mn960mn@163.com]
 */
@Slf4j
@Service
public class OrderBusiness extends JdbcTemplate {

    private RandomErrorAction ratioWith40 = new RandomErrorAction(70);

    public OrderBusiness(DataSource dataSource) {
        super(dataSource);
    }

    public Order insertOrder(CreateOrderReq req, String businessId) {
        Order order = new Order();
        order.setBusinessId(businessId);
        order.setUserId(req.getUserId());
        order.setPrice(req.getPrice());
        order.setStatus(100);
        order.setPayStatus(0);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator psc = conn -> {
            String sql = "insert into tb_order (business_id,user_id,price,status,pay_status,create_date)values(?,?,?,?,?,now())";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, order.getBusinessId());
            ps.setLong(2, req.getUserId());
            ps.setBigDecimal(3, req.getPrice());
            ps.setInt(4, order.getStatus());
            ps.setInt(5, order.getPayStatus());
            return ps;
        };
        this.update(psc, keyHolder);
        order.setOrderId(keyHolder.getKey().longValue());
        return order;
    }

    public void updateOrderPayStatus(Order order) {
        PreparedStatementSetter pss = ps -> {
            ps.setInt(1, 300);
            ps.setInt(2, 1);
            ps.setLong(3, order.getOrderId());
        };
        this.update("update tb_order set status=?,pay_status=?,edit_date=now() where order_id=?", pss);
        log.info("order[{}]已经处理完成", order.getOrderId());
    }

    public void setOrderFail(Order order) {
        PreparedStatementSetter pss = ps -> {
            ps.setInt(1, 500);
            ps.setInt(2, 2);
            ps.setLong(3, order.getOrderId());
        };
        this.update("update tb_order set status=?,pay_status=?,edit_date=now() where order_id=?", pss);
        log.info("order[{}]已更新为失败", order.getOrderId());
    }

    @RetryFunction(name = "RetryFunction重试-创建订单", identity = "demo.create.order", retryListener = "orderRetryListener", maxRetryCount = 6)
    public void payOrderAndUpdateStatus(Order order) {
        ratioWith40.doAction();
        updateOrderPayStatus(order);
    }
}