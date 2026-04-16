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

abstract class Command(val name: String, val aliases: List<String> = emptyList<String>()) {

  fun build(): List<LiteralArgumentBuilder<ClientSuggestionProvider>> {
    val mainRoot = LiteralArgumentBuilder.literal<ClientSuggestionProvider>(name)
    val functions = this::class.declaredFunctions

    for (function in functions) {
      function.isAccessible = true
      if (function.findAnnotation<DefaultHandler>() != null) {
        mainRoot.executes {
          function.call(this@Command)
          1
        }
        continue
      }
      if (function.findAnnotation<SubCommand>() != null) {
        mainRoot.then(buildSubCommand(function))
      }
    }

    val aliasRoots = aliases.filter { it.isNotBlank() }.map { alias ->
      val aliasRoot = LiteralArgumentBuilder.literal<ClientSuggestionProvider>(alias)
      mainRoot.arguments.forEach { child -> aliasRoot.then(child) }
      mainRoot.command?.let { aliasRoot.executes(it) }
      aliasRoot
    }

    return listOf(mainRoot) + aliasRoots
  }


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
