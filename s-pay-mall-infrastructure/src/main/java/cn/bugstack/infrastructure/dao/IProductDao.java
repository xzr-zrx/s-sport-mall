package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IProductDao {
    List<Product> searchOnSale(@Param("keyword") String keyword,
                               @Param("category") String category,
                               @Param("sort") String sort,
                               @Param("offset") int offset,
                               @Param("limit") int limit);
    long countOnSale(@Param("keyword") String keyword, @Param("category") String category);
    Product findById(@Param("id") Long id);
    int insert(Product product);
    int update(Product product);
    int decreaseStock(@Param("id") Long id, @Param("quantity") int quantity);
    int increaseStock(@Param("id") Long id, @Param("quantity") int quantity);
}
