package softuni.productshop.service;

import softuni.productshop.domain.models.service.RoleServiceModel;

import java.util.Set;

public interface RoleService {
    void seedRolesInDB();
    Set<RoleServiceModel>findAllRoles();
    RoleServiceModel findByAuthority(String authority);
}
