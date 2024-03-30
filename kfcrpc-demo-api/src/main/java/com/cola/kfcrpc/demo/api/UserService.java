package com.cola.kfcrpc.demo.api;

public interface UserService {
   User findById(Integer id);

   User findById(int id, String name);
}
