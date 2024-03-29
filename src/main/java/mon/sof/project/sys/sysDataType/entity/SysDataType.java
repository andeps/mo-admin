package mon.sof.project.sys.sysDataType.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author zxm
 * @since 2020-12-17
 */
public class SysDataType implements Serializable {


    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据类型编码
     */
    private String code;

    /**
     * 默认长度
     */
    private Integer defaultLength;

    /**
     * 默认精度
     */
    private Integer defaultPrecision;

    /**
     * 排序
     */
    private Integer sort;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getDefaultLength() {
        return defaultLength;
    }

    public void setDefaultLength(Integer defaultLength) {
        this.defaultLength = defaultLength;
    }

    public Integer getDefaultPrecision() {
        return defaultPrecision;
    }

    public void setDefaultPrecision(Integer defaultPrecision) {
        this.defaultPrecision = defaultPrecision;
    }
}
