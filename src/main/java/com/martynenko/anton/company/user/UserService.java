package com.martynenko.anton.company.user;

import com.martynenko.anton.company.utils.DateInterval;
import java.util.Collection;
import javax.validation.Valid;

public interface UserService {

  User create(UserDTO newUser);

  void create(@Valid Collection<UserDTO> newUsers);

  User update(Long id, UserDTO updated);

  User get(Long id);

  Collection<User> listAll();

  void delete(Long id);

  Collection<AvailableUserView> listAvailableUserViews();
  Collection<AvailableUserView> listAvailableUserViews(DateInterval targetPeriod);
  Collection<AvailableUserView> listAvailableUserViews(long period);

}
