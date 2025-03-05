# JavArgparse

## Usage

Create a class for your arguments, then apply the `@Argument` annotation to any field you want as an argument.

The name of the field is important. A field named `thisIsAnArg` will be turned into `--this-is-an-arg` (unless it is a positional argument).

`@Argument` has some options:

- `shortName`: a short version of the argument (e.g. the field `config` has `c` short name, so it has `--config` and `-c` as it's arguments). **Only single letter short names are allowed**, though a `String` has to be given.
- `optional`: non-optional arguments must be given, optional ones are optional.
- `positional`: a positional argument doesn't have the `--long-version`. Instead, it is passed by no starting with any `-`. **Positional arguments can't be optional.**
- `type`: type determines how the argument is handled:
  - `DEFAULT`: save what has been passed
  - `COUNT`: count the amount of times this argument is passed
  - `TRUE_IF_PRESENT`: sets the argument to true, if it is present; false otherwise
  - `FALSE_IF_PRESENT`: same as `TRUE_IF_PRESENT`, just reversed

Optional arguments which are not passed will have their default value (`int`: 0, `Object`: `null`, etc. - **Object includes String, meaning a String wil be null**).

Invalid argument combinations (e.g. positional and optional) are checked whenever the ArgumentParser is created.

### Argument Types

All basic inbuilt types as well as `Path` and `File` are implemented by default.

Custom types may be used as well, however it is required to register them before parsing.

To do this, create an ArgumentParser and then register it with `registerTypeConverter`. A method turning a string into that class is required.
