
DefaultBehavior cell("") = method(
  "returns result of evaluating first argument", 
  n, 

  ; We need to use cell here, to not activate a method-like object
  cell(:n))

DefaultBehavior - = method(
  "returns the negation of the argument", 
  obj, 

  obj negation)

DefaultBehavior do = macro(
  "executes the arguments with the receiver as context and ground, and then returns the receiver.", 

  call arguments each(evaluateOn(cell("@")))
  cell("@"))

DefaultBehavior fnx = macro(
  "does the same things as fn, but returns something that is activatable.",

  call resendToMethod("fn") do(
    activatable = true))

DefaultBehavior derive = method(
  "calls mimic.", 

  mimic)

DefaultBehavior list = macro(
  "Takes zero or more arguments and returns a newly created list containing the result of evaluating these arguments",

  call evaluatedArguments)

DefaultBehavior aliasMethod("list", "[]")

DefaultBehavior with = macro(
  "takes any number of keyword arguments, followed by an optional code argument. will first create a new mimic of the receiver, then evaluate all the keyword arguments in order and set cells corresponding to the names of these keyword arguments to the evaluated arguments. if a code argument is supplied, it will be evaluated in the context of the newly created object, using something similar to 'do'. returns the created object.",
  
  newObject = mimic
  call arguments each(arg, 
    if(arg keyword?, 
      newObject cell(arg name asText[0..-2]) = arg next evaluateOn(call ground),
      newObject doMessage(arg)))
  newObject)

DefaultBehavior warn! = method(
  "takes the same kind of arguments as 'signal!', and will signal a condition. the default condition used is Condition Warning Default. a restart called 'ignore' will be established. if no rescue or restart is invoked warn! will report a warning to System err.",
  datum, +:krest,

  if(datum kind?("Text"), 
    datum = Condition Warning Default with(text: datum))
  bind(
    restart(ignore, fn(datum)),

    result = signal!(datum, *krest)
    System err println("WARNING: #{result report}")
    result))

;; TODO: test all this behavior
DefaultBehavior do(
  Dict addKeysAndValues = method(
    "zips the keys and the values together into this dict",
    keys, values, 

    keys each(i, k, self[k] = values[i])
    self)

  cellDescriptionDict = method(
    "returns a dict containing each cell and it's corresponding description",

	cellNames = cell(:self) cellNames sort
	cellDescs = cellNames map(name, cell(:self) cell(name) notice)
	Dict mimic addKeysAndValues(cellNames, cellDescs))

  cellSummary = method(
    "returns a representation of the current object that includes information about it's cells",

    s = " #{cell(:self) notice}:\n"
    cellDescriptions = cellDescriptionDict
	cellDescriptions keys sort each(k,
	  s = s + ("  ", k alignLeft(16), " = ", cellDescriptions[k], "\n"))
    s
  )

  inspect = cell(:cellSummary)

  notice = method(
    "returns the default notice text for this object",

    "#{cell(:self) kind}_#{cell(:self) uniqueHexId}"))
