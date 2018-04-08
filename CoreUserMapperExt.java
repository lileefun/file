package cn.owhat.business.dao.mapper;

import cn.owhat.business.dao.model.CoreUserInfoEntity;
import cn.owhat.business.dao.model.UserTongJiResultEntity;
import cn.owhat.business.service.user.model.AuthUserDTO;
import cn.owhat.db.owhat.dao.model.CoreUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by libin on 2016/12/8.
 */
public interface CoreUserMapperExt {

    @Select({"<script>SELECT t1.* FROM `core_user` t1 INNER JOIN `core_user_stat` t2 ON ",
            " t1.`user_id` = t2.`fk_user_id` WHERE t1.`active` = 1 and t1.`user_type` = #{userType} ",
            "<if test=\"notinUserIds != null\">",
            " and t1.`user_id` not in ",
            "<foreach collection='notinUserIds' item='userid' open='(' separator=',' close=')'>#{userid}</foreach>",
            "</if>",
            " ORDER BY t2.`participants` DESC ,t1.`create_at` DESC</script>"})
    @Results({
            @Result(column = "user_id", property = "userId", jdbcType = JdbcType.BIGINT, id = true),
            @Result(column = "user_name", property = "userName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "password", property = "password", jdbcType = JdbcType.VARCHAR),
            @Result(column = "salt", property = "salt", jdbcType = JdbcType.VARCHAR),
            @Result(column = "true_name", property = "trueName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "nick_name", property = "nickName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "mobile", property = "mobile", jdbcType = JdbcType.VARCHAR),
            @Result(column = "fk_area_country_id", property = "fkAreaCountryId", jdbcType = JdbcType.INTEGER),
            @Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
            @Result(column = "avatar_img", property = "avatarImg", jdbcType = JdbcType.VARCHAR),
            @Result(column = "cover_img", property = "coverImg", jdbcType = JdbcType.VARCHAR),
            @Result(column = "sex", property = "sex", jdbcType = JdbcType.TINYINT),
            @Result(column = "birthday", property = "birthday", jdbcType = JdbcType.DATE),
            @Result(column = "active", property = "active", jdbcType = JdbcType.SMALLINT),
            @Result(column = "create_at", property = "createAt", jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "update_at", property = "updateAt", jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "fk_creator_id", property = "fkCreatorId", jdbcType = JdbcType.BIGINT),
            @Result(column = "fk_updater_id", property = "fkUpdaterId", jdbcType = JdbcType.BIGINT),
            @Result(column = "level", property = "level", jdbcType = JdbcType.INTEGER),
            @Result(column = "fk_city_id", property = "fkCityId", jdbcType = JdbcType.INTEGER),
            @Result(column = "fk_prov_id", property = "fkProvId", jdbcType = JdbcType.INTEGER),
            @Result(column = "fk_district_id", property = "fkDistrictId", jdbcType = JdbcType.INTEGER),
            @Result(column = "client_type", property = "clientType", jdbcType = JdbcType.VARCHAR),
            @Result(column = "client_version", property = "clientVersion", jdbcType = JdbcType.VARCHAR),
            @Result(column = "user_type", property = "userType", jdbcType = JdbcType.SMALLINT),
            @Result(column = "description", property = "description", jdbcType = JdbcType.VARCHAR),
            @Result(column = "company", property = "company", jdbcType = JdbcType.VARCHAR),
            @Result(column = "works", property = "works", jdbcType = JdbcType.VARCHAR),
            @Result(column = "brand_speak", property = "brandSpeak", jdbcType = JdbcType.VARCHAR),
            @Result(column = "sort_weight", property = "sortWeight", jdbcType = JdbcType.INTEGER),
            @Result(column = "fans_nickname", property = "fansNickname", jdbcType = JdbcType.VARCHAR),
            @Result(column = "fk_org_user_id", property = "fkOrgUserId", jdbcType = JdbcType.BIGINT),
            @Result(column = "area_cate", property = "areaCate", jdbcType = JdbcType.VARCHAR),
            @Result(column = "message_push", property = "messagePush", jdbcType = JdbcType.SMALLINT),
            @Result(column = "is_auto_share", property = "isAutoShare", jdbcType = JdbcType.SMALLINT),
            @Result(column = "md5_password", property = "md5Password", jdbcType = JdbcType.VARCHAR),
            @Result(column = "signature", property = "signature", jdbcType = JdbcType.LONGVARCHAR)
    })
    public List<CoreUser> findUsersByuserType(@Param("userType") Integer userType, @Param("notinUserIds") List<Long> notinUserIds);

    /**
     * 统计创建时间在time之后的商品数
     *
     * @param start .
     * @param end   .
     * @return
     */
    @Select({
            "<script>",
            "SELECT t1.user_type as userType, count(*) as total from core_user as t1",
            " where t1.active =1",
            "<if test='start != null'>and t1.create_at >= #{start}</if>",
            "<if test='end != null'>and #{end} > t1.create_at</if>",
            "GROUP BY t1.user_type",
            "</script>"
    })
    List<UserTongJiResultEntity> tongJiUserByTime(@Param("start") Date start, @Param("end") Date end);

    @Select({
            "<script>",
            "/*#mycat:db_type=master*/",
            "select", "user_id, user_name, password, salt, true_name, nick_name, mobile, fk_area_country_id, ", "email, avatar_img, cover_img, sex, birthday, active, create_at, update_at, fk_creator_id, ", "fk_updater_id, level, fk_city_id, fk_prov_id, fk_district_id, client_type, client_version, ", "user_type, description, company, works, brand_speak, sort_weight, fans_nickname, ", "fk_org_user_id, area_cate, message_push, is_auto_share, md5_password, signature",
            "from core_user",
            "where email = #{account} or mobile = #{account}",
            "</script>"
    })
    @Results({
            @Result(column = "user_id", property = "userId", jdbcType = JdbcType.BIGINT, id = true),
            @Result(column = "user_name", property = "userName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "password", property = "password", jdbcType = JdbcType.VARCHAR),
            @Result(column = "salt", property = "salt", jdbcType = JdbcType.VARCHAR),
            @Result(column = "true_name", property = "trueName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "nick_name", property = "nickName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "mobile", property = "mobile", jdbcType = JdbcType.VARCHAR),
            @Result(column = "fk_area_country_id", property = "fkAreaCountryId", jdbcType = JdbcType.INTEGER),
            @Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
            @Result(column = "avatar_img", property = "avatarImg", jdbcType = JdbcType.VARCHAR),
            @Result(column = "cover_img", property = "coverImg", jdbcType = JdbcType.VARCHAR),
            @Result(column = "sex", property = "sex", jdbcType = JdbcType.TINYINT),
            @Result(column = "birthday", property = "birthday", jdbcType = JdbcType.DATE),
            @Result(column = "active", property = "active", jdbcType = JdbcType.SMALLINT),
            @Result(column = "create_at", property = "createAt", jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "update_at", property = "updateAt", jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "fk_creator_id", property = "fkCreatorId", jdbcType = JdbcType.BIGINT),
            @Result(column = "fk_updater_id", property = "fkUpdaterId", jdbcType = JdbcType.BIGINT),
            @Result(column = "level", property = "level", jdbcType = JdbcType.INTEGER),
            @Result(column = "fk_city_id", property = "fkCityId", jdbcType = JdbcType.INTEGER),
            @Result(column = "fk_prov_id", property = "fkProvId", jdbcType = JdbcType.INTEGER),
            @Result(column = "fk_district_id", property = "fkDistrictId", jdbcType = JdbcType.INTEGER),
            @Result(column = "client_type", property = "clientType", jdbcType = JdbcType.VARCHAR),
            @Result(column = "client_version", property = "clientVersion", jdbcType = JdbcType.VARCHAR),
            @Result(column = "user_type", property = "userType", jdbcType = JdbcType.SMALLINT),
            @Result(column = "description", property = "description", jdbcType = JdbcType.VARCHAR),
            @Result(column = "company", property = "company", jdbcType = JdbcType.VARCHAR),
            @Result(column = "works", property = "works", jdbcType = JdbcType.VARCHAR),
            @Result(column = "brand_speak", property = "brandSpeak", jdbcType = JdbcType.VARCHAR),
            @Result(column = "sort_weight", property = "sortWeight", jdbcType = JdbcType.INTEGER),
            @Result(column = "fans_nickname", property = "fansNickname", jdbcType = JdbcType.VARCHAR),
            @Result(column = "fk_org_user_id", property = "fkOrgUserId", jdbcType = JdbcType.BIGINT),
            @Result(column = "area_cate", property = "areaCate", jdbcType = JdbcType.VARCHAR),
            @Result(column = "message_push", property = "messagePush", jdbcType = JdbcType.SMALLINT),
            @Result(column = "is_auto_share", property = "isAutoShare", jdbcType = JdbcType.SMALLINT),
            @Result(column = "md5_password", property = "md5Password", jdbcType = JdbcType.VARCHAR),
            @Result(column = "signature", property = "signature", jdbcType = JdbcType.LONGVARCHAR)
    })
    List<CoreUser> findUserByAccountByMaster(@Param("account") String account);

    @Select({
            "<script>",
            "SELECT ",
            "   u.user_id ",
            "FROM ",
            "   core_user AS u ",
            "INNER JOIN core_user_stat AS c ON u.user_id = c.fk_user_id ",
            "AND u.user_type = 5 ",
            "AND u.active = 1 ",
            "<if test='areaCate!=null'>",
            "AND u.area_cate = #{areaCate} ",
            "</if>",
            "ORDER BY ",
            "   c.participants DESC",
            "</script>"
    })
    List<Long> findStarsByParticipants(@Param("areaCate") String areaCate);

    @Select({
            "SELECT u.area_cate from core_user as u where u.user_type = 5 GROUP BY u.area_cate ORDER BY count(*) desc"
    })
    List<String> findAreaCate();


    // @Select({"select t1.user_id id,t1.nick_name nicakName,t1.create_at createTime,t3.am payStream ,t3.c orderNum ,t3.pu payUserNum,t5.pfc payFansNum,t4.fc fansNum, t5.pfc/t4.fc payFansRate from core_user t1  left join (select count(1) c,sum(total_amount) am ,count(distinct fk_user_id) pu,fk_goods_owner_id from shop_order where currency_type = 1 and status in (3,4) and out_pay_at>= #{startTime} and out_pay_at < #{endTime} group by fk_goods_owner_id) t3 on t1.user_id = t3.fk_goods_owner_id left join (select count(1) fc,b_id from core_follows where b_type in (2,3) and create_at <=#{endTime} group by b_id)  t4 on t1.user_id = t4.b_id left join (select count(distinct tt2.a_id) pfc, tt1.fk_goods_owner_id from shop_order tt1 left join core_follows tt2 on tt2.a_id = tt1.fk_user_id and tt2.b_id = tt1.fk_goods_owner_id where tt1.currency_type = 1 and tt1.status in (3,4)  and tt2.b_type in (2,3) and tt2.create_at <=#{startTime} group by tt1.fk_goods_owner_id) t5 on t5.fk_goods_owner_id = t1.user_id where t1.user_type in (2,3) and t1.active = 1  order by t1.user_id desc;"})
    @Select({"select t1.user_id id,t1.nick_name nicakName,t1.create_at createTime,t3.am payStream ,t3.c orderNum ,t3.pu payUserNum,t5.pfc payFansNum,t4.fc fansNum, t5.pfc/t4.fc payFansRate from core_user t1  left join (select count(1) c,sum(total_amount) am ,count(distinct fk_user_id) pu,fk_goods_owner_id from shop_order where currency_type = 1 and status in (3,4)and out_pay_at>= #{startTime} and out_pay_at < #{endTime} group by fk_goods_owner_id) t3 on t1.user_id = t3.fk_goods_owner_id left join (select count(1) fc,b_id from core_follows where b_type in (2,3) and create_at <=#{endTime} group by b_id)  t4 on t1.user_id = t4.b_id left join (select count(distinct tt2.a_id) pfc, tt1.fk_goods_owner_id from shop_order tt1 left join core_follows tt2 on tt2.a_id = tt1.fk_user_id and tt2.b_id = tt1.fk_goods_owner_id where tt1.currency_type = 1 and tt1.status in (3,4)  and tt2.b_type in (2,3) and tt2.create_at <=#{endTime} and tt1.out_pay_at>= #{startTime} and tt1.out_pay_at < #{endTime} group by tt1.fk_goods_owner_id) t5 on t5.fk_goods_owner_id = t1.user_id where t1.user_type in (2,3) and t1.active = 1  order by t1.user_id desc;"})
    List<CoreUserInfoEntity> selectFanceInfo(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Select({
            "<script>",
            "SELECT t1.user_id as userId, t1.nick_name as nickName, IFNULL(t2.participants, 0) as fansCount, IFNULL(SUM(t3.sale_amount), 0) as goodsSaleAmount from core_user as t1 ",
            "inner JOIN core_user_stat as t2 on t1.user_id = t2.fk_user_id and t1.active = 1 and t1.user_type = 2 ",
            "left JOIN shop_goods as t3 on t1.user_id = t3.fk_owner_id and t3.currency_type=1",
            "<if test='userIds!=null'>",
            " where t1.user_id in <foreach collection='userIds' item='userId' open='(' separator=',' close=')'>#{userId}</foreach>",
            "</if>",
            " GROUP BY t1.user_id ",
            "<if test='fansCount!=null and goodsSaleAmount!=null'>",
            " HAVING fansCount > #{fansCount} and goodsSaleAmount > #{goodsSaleAmount}",
            "</if>",
            "<if test='fansCount!=null and goodsSaleAmount == null'>",
            " HAVING fansCount > #{fansCount}",
            "</if>",
            "<if test='fansCount == null and goodsSaleAmount!=null'>",
            " HAVING goodsSaleAmount > #{goodsSaleAmount}",
            "</if>",
            "</script>"
    })
    List<AuthUserDTO> findClubWithFilter(@Param("userIds") List<Long> userIds, @Param("fansCount") Long fansCount, @Param("goodsSaleAmount") BigDecimal goodsSaleAmount);

}
