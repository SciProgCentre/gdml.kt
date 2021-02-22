#!/usr/bin/python
"""
"""
import sys
import xml.dom.minidom as md


class Vars:
    # Dict of variables defined in Gdml file
    def __init__(self, defines):
        self.var = {}
        for v in defines:
            nm = v.attributes['name'].value
            if( nm in self.var ):
                raise Exception("Duplicate name: " + name)
            if v.tagName == 'constant' or v.tagName == 'variable':
                expr = v.attributes['value'].value
                self.var[nm] = self.eval(expr)
    def eval(self, expr):
        "Evaluate string"
        return eval(expr, {}, self.var)


def rewrite(dom, var) :
    # Rewrite DOM in place
    for elem in dom.documentElement.childNodes:
        if type(elem) is md.Element:
            for subelem in elem.childNodes:
                if type(subelem) is md.Element:
                    for key in subelem.attributes.keys():
                        if key != 'name':
                            try:
                                expr = subelem.attributes.get(key)
                                subelem.attributes[key] = str(var.eval(expr.value))
                            except Exception:
                                pass

def main():
    filepath = sys.argv[1]
    dom = md.parse(filepath)

    # Read all variables
    defines = [d
        for node in dom.documentElement.getElementsByTagName('define')
        for d in node.childNodes
        if type(d) is md.Element
        ]
    var = Vars(defines)

    # Now modify DOM in place
    rewrite(dom, var)
    
    # Write to file
    with open(f'{filepath[:-5]}_modified.gdml', 'w') as f:
        f.write(dom.toprettyxml())


if __name__ == "__main__":
    main()
