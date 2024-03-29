package com.github.andylke.demo.randomuser;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.web.client.RestTemplate;

public class RandomUserRestServiceReader implements ItemReader<RandomUser> {

  private RestTemplate restTemplate = new RestTemplate();

  private Queue<RandomUser> randomUsers = new LinkedList<RandomUser>();

  private int currentPage = 1;

  private final int totalPage;

  private final int pageSize;

  public RandomUserRestServiceReader(int totalPage, int pageSize) {
    this.totalPage = totalPage;
    this.pageSize = pageSize;
  }

  @Override
  public RandomUser read()
      throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    if (randomUsers.isEmpty()) {
      if (currentPage > totalPage) {
        return null;
      }
      randomUsers.addAll(getRandomUsers(currentPage++, pageSize));
    }
    return randomUsers.poll();
  }

  private List<RandomUser> getRandomUsers(int page, int size) {
    return restTemplate
        .getForEntity(
            "https://randomuser.me/api/?inc=name,location,gender,email,password,login,nat&page="
                + page
                + "&results="
                + size,
            RandomUserResponse.class)
        .getBody()
        .getResults();
  }
}
