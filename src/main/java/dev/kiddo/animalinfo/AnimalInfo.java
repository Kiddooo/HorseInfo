/* Licensed under the <LICENSE> */
package dev.kiddo.animalinfo;

import dev.kiddo.animalinfo.client.AnimalInfoCommandHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class AnimalInfo implements ModInitializer {

  @Override
  public void onInitialize() {
    ClientCommandRegistrationCallback.EVENT.register(
        ((dispatcher, buildContext) -> AnimalInfoCommandHandler.register(dispatcher)));
  }
}
