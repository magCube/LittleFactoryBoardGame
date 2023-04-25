package org.magcube.controller;

import graphql.com.google.common.base.Strings;
import org.magcube.GameInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/game")
public class GameInstanceController {

  @GetMapping("/")
  public GameInstance getGameInstance(@RequestParam(value = "id") String id) {
    if (Strings.isNullOrEmpty(id)) {
      throw new IllegalArgumentException("missing id!");
    }
    //TODO: to find the real gameInstance from cache/DB and return
    return new GameInstance();
  }

  @GetMapping("/list")
  public GameInstance listGameInstances(@RequestParam(value = "playerId") String playerId) {
    if (Strings.isNullOrEmpty(playerId)) {
      throw new IllegalArgumentException("missing playerId!");
    }
    //TODO: to find the real gameInstance from cache/DB and return
    return new GameInstance();
  }

  @PostMapping("/")
  public ResponseEntity createGameInstance() {
    //TODO create a game instance with the params and return it
    //after received should run in background asynchronously to create game instance and return by websocket when finishes/failed
    return ResponseEntity.ok(HttpStatus.OK);
  }

}
