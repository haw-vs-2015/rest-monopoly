# rest-monopoly #

## Build & Run ##

```sh
$ cd rest-monopoly
$ ./sbt
> container:start
> browse
```


mergeStrategy in assembly <<= (mergeStrategy in assembly) {
  (old) => {
    case "about.html"     => MergeStrategy.discard
    case x => old(x)
  }
}


If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.
