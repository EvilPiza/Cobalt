package org.cobalt.command

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.isAccessible
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import org.cobalt.command.annotation.DefaultHandler
import org.cobalt.command.annotation.SubCommand

/**
 * Base class for chat commands with option for subcommands.
 *
 * @property name primary command name
 * @property aliases alternate command names
 */
abstract class Command(val name: String, val aliases: List<String> = emptyList<String>()) {

  internal fun build(): List<LiteralArgumentBuilder<ClientSuggestionProvider>> {
    val mainRoot = LiteralArgumentBuilder.literal<ClientSuggestionProvider>(name)

    registerFunctions(mainRoot)

    return listOf(mainRoot) + buildAliases(mainRoot)
  }

  private fun registerFunctions(mainRoot: LiteralArgumentBuilder<ClientSuggestionProvider>) {
    for (function in this::class.declaredFunctions) {
      function.isAccessible = true

      if (function.findAnnotation<DefaultHandler>() != null) {
        mainRoot.executes {
          function.call(this@Command)
          return@executes 1
        }
        continue
      }

      if (function.findAnnotation<SubCommand>() != null) {
        mainRoot.then(buildSubCommand(function))
      }
    }
  }

  private fun buildAliases(
    mainRoot: LiteralArgumentBuilder<ClientSuggestionProvider>,
  ): List<LiteralArgumentBuilder<ClientSuggestionProvider>> {
    return aliases.filter { it.isNotBlank() }.map { alias ->
      val aliasRoot = LiteralArgumentBuilder.literal<ClientSuggestionProvider>(alias)
      mainRoot.arguments.forEach { child -> aliasRoot.then(child) }
      mainRoot.command?.let { aliasRoot.executes(it) }
      aliasRoot
    }
  }

  private fun buildSubCommand(function: KFunction<*>): LiteralArgumentBuilder<ClientSuggestionProvider> {
    val literal = LiteralArgumentBuilder.literal<ClientSuggestionProvider>(function.name)

    val parameters = function.parameters
    val valueParams = parameters.filter { it.kind == KParameter.Kind.VALUE }

    return if (valueParams.isEmpty()) buildNoArgSubCommand(literal, function)
    else buildArgSubCommand(literal, function, parameters, valueParams)
  }

  private fun buildNoArgSubCommand(
    literal: LiteralArgumentBuilder<ClientSuggestionProvider>,
    function: KFunction<*>,
  ): LiteralArgumentBuilder<ClientSuggestionProvider> {
    literal.executes {
      function.call(this@Command)
      return@executes 1
    }

    return literal
  }

  private fun buildArgSubCommand(
    literal: LiteralArgumentBuilder<ClientSuggestionProvider>,
    function: KFunction<*>,
    parameters: List<KParameter>,
    valueParams: List<KParameter>,
  ): LiteralArgumentBuilder<ClientSuggestionProvider> {
    val arguments = valueParams.mapIndexed { index, param ->
      val name = param.name ?: "argument$index"
      createArgument(name, param.type.classifier)
    }

    arguments.last().executes { ctx ->
      executeFunctionWithContext(function, parameters, valueParams, ctx)
      return@executes 1
    }

    val argumentTree = buildArgumentTree(arguments)

    return literal.then(argumentTree)
  }

  private fun buildArgumentTree(
    arguments: List<RequiredArgumentBuilder<ClientSuggestionProvider, *>>,
  ): RequiredArgumentBuilder<ClientSuggestionProvider, *> {
    return arguments.reduceRight { arg, acc -> arg.then(acc) }
  }

  private fun executeFunctionWithContext(
    function: KFunction<*>,
    parameters: List<KParameter>,
    valueParams: List<KParameter>,
    ctx: CommandContext<ClientSuggestionProvider>,
  ) {
    val mappedValues = valueParams.mapIndexed { index, param ->
      val argumentName = param.name ?: "argument$index"
      valueExtractors[param.type.classifier]?.invoke(ctx, argumentName)
        ?: error("Unsupported type: ${param.type}")
    }

    val argsMap = mutableMapOf<KParameter, Any?>()

    val instanceParam = parameters.firstOrNull { it.kind == KParameter.Kind.INSTANCE }
    if (instanceParam != null) argsMap[instanceParam] = this@Command

    for (i in valueParams.indices) {
      argsMap[valueParams[i]] = mappedValues[i]
    }

    function.callBy(argsMap)
  }

  private fun createArgument(
    name: String,
    type: Any?,
  ): RequiredArgumentBuilder<ClientSuggestionProvider, *> {
    val argType = argumentTypeSuppliers[type]?.invoke()
      ?: throw IllegalArgumentException("Unsupported parameter type: $type")

    return RequiredArgumentBuilder.argument(name, argType)
  }

  private val argumentTypeSuppliers: Map<Any?, () -> com.mojang.brigadier.arguments.ArgumentType<*>> = mapOf(
    Double::class to { DoubleArgumentType.doubleArg() },
    Int::class to { IntegerArgumentType.integer() },
    String::class to { StringArgumentType.word() },
    Boolean::class to { BoolArgumentType.bool() },
    Float::class to { FloatArgumentType.floatArg() },
  )

  private val valueExtractors: Map<Any?, (CommandContext<ClientSuggestionProvider>, String) -> Any?> = mapOf(
    Double::class to { c, n -> DoubleArgumentType.getDouble(c, n) },
    Int::class to { c, n -> IntegerArgumentType.getInteger(c, n) },
    String::class to { c, n -> StringArgumentType.getString(c, n) },
    Boolean::class to { c, n -> BoolArgumentType.getBool(c, n) },
    Float::class to { c, n -> FloatArgumentType.getFloat(c, n) },
  )

}
