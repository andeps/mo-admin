package mon.sof.project.sys.sysRole.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Optional;
import mon.sof.common.orm.Resp;
import mon.sof.project.sys.sysLog.advice.Log;
import mon.sof.project.sys.sysMenu.entity.SysMenu;
import mon.sof.project.sys.sysMenu.service.SysMenuService;
import mon.sof.project.sys.sysMenu.util.TreeNodeHandle;
import mon.sof.project.sys.sysRole.entity.RoleAuthorityEnum;
import mon.sof.project.sys.sysRole.entity.RoleStatusEnum;
import mon.sof.project.sys.sysRole.entity.SysRole;
import mon.sof.project.sys.sysUser.entity.SysUser;
import mon.sof.project.sys.sysUser.service.SysUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import mon.sof.project.sys.sysRole.service.SysRoleService;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zxm
 * @since 2020-10-15
 */
@RestController
@RequestMapping("/sysRole/sys-role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysUserService sysUserService;


    /**
     * 获取后台角色信息
     *
     * @Author zhangxiaomei
     * @Date 2020-08-28 14:49:26
     * @Param
     * @Return
     */
    @PostMapping("/getSysRoleInfo")
    public Resp getSysRoleInfo(
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<SysRole> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.orderByAsc("orders");
        List<SysRole> list = sysRoleService.list(objectQueryWrapper);
        for (SysRole sysRole : list) {
            Optional<Integer> status = Optional.fromNullable(sysRole.getStatus());
            Optional<Integer> authority = Optional.fromNullable(sysRole.getAuthority());
            sysRole.setStatusName(RoleStatusEnum.findNameByCode(status.or(0)));
            sysRole.setAuthorityName(RoleAuthorityEnum.findNameByCode(authority.or(0)));
        }
        PageInfo<SysRole> sysRolePageInfo = new PageInfo<>(list);
        return Resp.ok(sysRolePageInfo);
    }


    /**
     * 查询菜单
     *
     * @Author zhangxiaomei
     * @Date 2020-08-31 13:27:49
     * @Param
     * @Return
     */
    @PostMapping("/findMenuInfo")
    public Resp findMenuInfo(@RequestParam Long id) {
        SysRole byId = sysRoleService.getById(id);
        QueryWrapper<SysMenu> sysMenuQueryWrapper = new QueryWrapper<>();
        sysMenuQueryWrapper.eq("func_type", "0");
        List<SysMenu> list = sysMenuService.list(sysMenuQueryWrapper);
        List<Tree<String>> trees = TreeNodeHandle.handleMenu(list);
        if (byId.getCode().equals("Admin")) {
            return Resp.ok("admin", trees);
        }
        return Resp.ok(trees);
    }


    /**
     * 修改菜单权限
     *
     * @Author zhangxiaomei
     * @Date 2020-08-31 14:54:55
     * @Param
     * @Return
     */
    @Log(func = "角色管理", remarks = "修改权限")
    @PostMapping("/updateRoleMenu")
    public Resp updateRoleMenu(@RequestParam Long id,
                               @RequestParam String menuIds) {
        SysRole byId = sysRoleService.getById(id);
        if (StrUtil.isEmpty(menuIds)) {
            byId.setMenuIds(null);
        } else {
            byId.setMenuIds(menuIds);
        }
        sysRoleService.updateById(byId);
        return Resp.ok();

    }


    /**
     * 删除角色
     *
     * @Author zhangxiaomei
     * @Date 2020-09-01 11:05:24
     * @Param
     * @Return
     */

    @Log(func = "角色管理", remarks = "删除角色")
    @PostMapping("/deleteRole")
    public Resp deleteRole(@RequestParam Long id) {
        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
        sysUserQueryWrapper.eq("role_id", id);
        List<SysUser> list = sysUserService.list(sysUserQueryWrapper);
        if (list.size() > 0) {
            return Resp.err("有用户绑定此角色，请先解绑用户角色后在删除角色！");
        }
        SysRole byId = sysRoleService.getById(id);
        if (byId.getCode().equals("Admin")) {
            return Resp.err("当前角色为超级管理员角色，无法删除此角色！");
        }
        sysRoleService.removeById(id);
        return Resp.ok();
    }


    /**
     * 根据id获取角色信息
     *
     * @Author zhangxiaomei
     * @Date 2020-09-01 11:52:19
     * @Param
     * @Return
     */
    @PostMapping("/finRoleById")
    public Resp finRoleById(@RequestParam Long id) {
        SysRole byId = sysRoleService.getById(id);
        return Resp.ok(byId);
    }


    /**
     * 新增角色
     *
     * @Author zhangxiaomei
     * @Date 2020-09-01 17:08:14
     * @Param
     * @Return
     */
    @Log(func = "角色管理", remarks = "添加角色")
    @PostMapping("/addRole")
    public Resp addRole(SysRole sysRole) {
        sysRole.setStatus(RoleStatusEnum.findCodeByName(sysRole.getStatusName()));
        sysRole.setAuthority(RoleAuthorityEnum.findCodeByName(sysRole.getAuthorityName()));
        QueryWrapper<SysRole> sysRoleQueryWrapper = new QueryWrapper<>();
        sysRoleQueryWrapper.eq("code", sysRole.getCode());
        List<SysRole> list = sysRoleService.list(sysRoleQueryWrapper);
        if (list.size() > 0) {
            return Resp.err("角色编码不允许重复");
        }
        sysRoleService.save(sysRole);
        return Resp.ok();
    }

    /**
     * 修改角色
     *
     * @Author zhangxiaomei
     * @Date 2020-09-01 17:08:14
     * @Param
     * @Return
     */
    @Log(func = "角色管理", remarks = "修改角色")
    @PostMapping("/upRole")
    public Resp upRole(SysRole sysRole) {
        sysRole.setStatus(RoleStatusEnum.findCodeByName(sysRole.getStatusName()));
        sysRole.setAuthority(RoleAuthorityEnum.findCodeByName(sysRole.getAuthorityName()));
        if (sysRole.getCode().equals("Admin")) return Resp.err("当前角色是超级管理员角色，不允许修改！");
        sysRoleService.updateById(sysRole);
        return Resp.ok();
    }


}

