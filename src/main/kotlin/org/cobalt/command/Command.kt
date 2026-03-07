package org.cobalt.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.isAccessible
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import org.cobalt.command.annotation.DefaultHandler
import org.cobalt.command.annotation.SubCommand

abstract class Command(
  val name: String,
  val aliases: List<String> = emptyList(),
) {

  internal fun dispatch(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
    val rootNames = listOf(name) + aliases
    rootNames.forEach { rootName ->
      var root = literal(rootName)

      this::class.declaredMemberFunctions
        .find { it.findAnnotation<DefaultHandler>() != null }
        ?.let { method ->
          method.isAccessible = true
          root = attachExecution(root, method)
        }

      this::class.declaredMemberFunctions.forEach { method ->
        method.findAnnotation<SubCommand>()?.let {
          method.isAccessible = true
          root = root.then(attachExecution(literal(method.name), method))
        }
      }

      dispatcher.register(root)
    }
  }

  private fun attachExecution(
    builder: LiteralArgumentBuilder<FabricClientCommandSource>,
    method: KFunction<*>,
  ): LiteralArgumentBuilder<FabricClientCommandSource> {
    val params = method.parameters.drop(1)
    if (params.isEmpty()) return builder.executes { method.call(this); 1 }
    return builder.then(buildArguments(params, 0, method))
  }

  private fun buildArguments(
    params: List<KParameter>,
    index: Int,
    method: KFunction<*>,
  ): RequiredArgumentBuilder<FabricClientCommandSource, *> {
    val argBuilder = argumentType(params[index], index)

    return if (index == params.lastIndex) {
      argBuilder.executes { ctx ->
        val args = params.mapIndexed { i, p -> getArgValue(ctx, p, i) }
        method.call(this, *args.toTypedArray())
        return@executes 1
      }
    } else {
      argBuilder.then(buildArguments(params, index + 1, method))
    }
  }

  private fun argumentType(param: KParameter, index: Int): RequiredArgumentBuilder<FabricClientCommandSource, *> {
    return when (param.type.classifier) {
      Int::class -> argument(param.name ?: "arg$index", IntegerArgumentType.integer())
      String::class -> argument(param.name ?: "arg$index", StringArgumentType.string())
      Double::class -> argument(param.name ?: "arg$index", DoubleArgumentType.doubleArg())
      Float::class -> argument(param.name ?: "arg$index", FloatArgumentType.floatArg())
      Boolean::class -> argument(param.name ?: "arg$index", BoolArgumentType.bool())
      else -> throw IllegalArgumentException("Unsupported parameter type: ${param.type}")
    }
  }

  private fun getArgValue(ctx: CommandContext<FabricClientCommandSource>, p: KParameter, i: Int): Any {
    return when (p.type.classifier) {
      Int::class -> IntegerArgumentType.getInteger(ctx, p.name ?: "arg$i")
      String::class -> StringArgumentType.getString(ctx, p.name ?: "arg$i")
      Double::class -> DoubleArgumentType.getDouble(ctx, p.name ?: "arg$i")
      Float::class -> FloatArgumentType.getFloat(ctx, p.name ?: "arg$i")
      Boolean::class -> BoolArgumentType.getBool(ctx, p.name ?: "arg$i")
      else -> throw IllegalArgumentException("Unsupported parameter type: ${p.type}")
    }
  }

}
