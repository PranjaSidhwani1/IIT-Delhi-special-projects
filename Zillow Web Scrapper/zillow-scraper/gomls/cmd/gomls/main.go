package main

import (
	"context"
	"flag"
	"fmt"
	"os"

	"github.com/google/subcommands"
)

func init() {
	flag.Bool("verbose", true, "print info level logs to stdout")
}

func main() {
	subcommands.ImportantFlag("verbose")
	subcommands.Register(subcommands.HelpCommand(), "")
	subcommands.Register(subcommands.FlagsCommand(), "")
	subcommands.Register(subcommands.CommandsCommand(), "")
	subcommands.Register(&ListCmd{}, "Lists items with optional output format")

	flag.Parse()
	ctx := context.Background()
	status := subcommands.Execute(ctx)
	if status != 0 {
		fmt.Println("Exiting with code:", status)
	}
	os.Exit(int(status))
}
