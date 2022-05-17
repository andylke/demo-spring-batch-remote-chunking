package com.github.andylke.demo.randomuser;

import java.net.InetAddress;

import org.springframework.batch.item.ItemProcessor;

import com.github.andylke.demo.user.User;

public class RandomUserToUserProcessor implements ItemProcessor<RandomUser, User> {

  @Override
  public User process(RandomUser item) throws Exception {
    final User user = new User();

    user.setUsername(item.getLogin().getUsername());
    user.setPassword(item.getLogin().getPassword());
    user.setName(
        item.getName().getTitle()
            + " "
            + item.getName().getFirst()
            + " "
            + item.getName().getLast());
    user.setEmail(item.getEmail());
    user.setNationality(item.getNat());

    user.setCreatedBy(InetAddress.getLocalHost().getHostAddress());

    return user;
  }
}
