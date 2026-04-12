package org.cobalt.command

import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.isAccessible
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import org.cobalt.command.annotation.DefaultHandler
import org.cobalt.command.annotation.SubCommand

/** Base class for defining chat commands; reflection is used to discover handlers and subcommands.
 *
 * @property name the primary literal name of this command
 */
abstract class Command(val name: String) {

  /** Build a Brigadier LiteralArgumentBuilder for this command, wiring discovered handlers and subcommands. */
  fun build(): LiteralArgumentBuilder<ClientSuggestionProvider> {
    val root = LiteralArgumentBuilder.literal<ClientSuggestionProvider>(name)
    val functions = this::class.declaredFunctions

    for (function in functions) {
      function.isAccessible = true

      if (function.findAnnotation<DefaultHandler>() != null) {
        root.executes {
          function.call(this)
          return@executes 1
        }
        continue
      }

      if (function.findAnnotation<SubCommand>() != null) {
        root.then(buildSubCommand(function))
      }
    }

    return root
  }

  /** Construct a subcommand literal from a handler function and its parameters. */
  private fun buildSubCommand(function: KFunction<*>): LiteralArgumentBuilder<ClientSuggestionProvider> {
    val literal = LiteralArgumentBuilder.literal<ClientSuggestionProvider>(function.name)
    val params = function.parameters.drop(1)

    if (params.isEmpty()) {
      literal.executes {
        function.call(this)
        return@executes 1
      }

      return literal
    }

    val arguments = params.mapIndexed { index, param ->
      val name = param.name ?: "argument$index"
      createArgument(name, param.type.classifier)
    }

    arguments.last().executes { ctx ->
      val mappedArgs = params.mapIndexed { index, param ->
        val argumentName = param.name ?: "argument$index"
        when (param.type.classifier) {
          Double::class -> DoubleArgumentType.getDouble(ctx, argumentName)
          Int::class -> IntegerArgumentType.getInteger(ctx, argumentName)
          String::class -> StringArgumentType.getString(ctx, argumentName)
          Boolean::class -> BoolArgumentType.getBool(ctx, argumentName)
          Float::class -> FloatArgumentType.getFloat(ctx, argumentName)
          else -> error("Unsupported type: ${param.type}")
        }
      }

      function.call(this, *mappedArgs.toTypedArray())
      return@executes 1
    }

    val argumentTree = arguments.reduceRight { arg, acc ->
      arg.then(acc)
    }

    return literal.then(argumentTree)
  }

  /** Create a Brigadier RequiredArgumentBuilder for a supported parameter type. */
  private fun createArgument(
    name: String,
    type: Any?,
  ): RequiredArgumentBuilder<ClientSuggestionProvider, *> {
    return when (type) {
      Double::class -> RequiredArgumentBuilder.argument(name, DoubleArgumentType.doubleArg())
      Int::class -> RequiredArgumentBuilder.argument(name, IntegerArgumentType.integer())
      String::class -> RequiredArgumentBuilder.argument(name, StringArgumentType.word())
      Boolean::class -> RequiredArgumentBuilder.argument(name, BoolArgumentType.bool())
      Float::class -> RequiredArgumentBuilder.argument(name, FloatArgumentType.floatArg())
      else -> throw IllegalArgumentException("Unsupported parameter type: $type")
    }
  }

}
