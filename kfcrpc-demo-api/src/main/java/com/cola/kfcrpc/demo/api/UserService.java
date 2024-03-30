package com.cola.kfcrpc.demo.api;

import java.util.LinkedHashMap;
import java.util.List;

public interface UserService {
   User findById(Integer id);

   User findById(int id, String name);

    String getName();
    String getName(int id);

    long getId(long i);
    long getId(float i);

    long getId(User user);

    long[] getLongIds();

    long[] getIds(int[] ints);

    List<LinkedHashMap<String, String>> getIds(List<User> ints);
}
