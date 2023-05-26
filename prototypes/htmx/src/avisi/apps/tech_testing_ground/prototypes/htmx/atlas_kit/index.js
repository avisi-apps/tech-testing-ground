import React from "react"
import DropdownMenu, {DropdownItem} from '@atlaskit/dropdown-menu';

import {
  byAttrVal,
  byChildrenRefArray,
  byContent,
  byContentVal,
  createCustomElement,
  DOMModel,
  reactChildren
} from "@adobe/react-webcomponent";

class DropdownItemModel extends DOMModel {
  type = "dropdown-item"
  // @byContentVal() inner;
  @byAttrVal() label;
  @byAttrVal('custom-attributes') customAttributes;
}

class CustomDropdownItem extends React.Component {
  constructor(props) {
    // console.log("item props")
    // console.log(props)
    super(props);
  }

  render() {
    return <DropdownItem
      onClick={(e) => console.log('item clicked in react')}
    >{this.props.label}</DropdownItem>
  }
}


const DropdownItemCustomElement = createCustomElement(CustomDropdownItem, DropdownItemModel, "element");

customElements.define("dropdown-item-custom-element", DropdownItemCustomElement);

class DropdownModel extends DOMModel {
  @byAttrVal() trigger;
  // @byChildrenRefArray("dropdown-item-custom-element", DropdownItemModel) childVals;
  @reactChildren('.dropdown-item') dropdownItems
  // @byContent('.dropdown-item') dropdownItem
  // @byChildContentVal("dropdown-item-custom-element") dropdownItem;
}

class CustomDropdownMenu extends React.Component {
  constructor(props) {
    // console.log("menu props")
    // console.log(props)
    super(props);
  }

  shouldComponentUpdate(nextProps, nextState, nextContext) {
    return false;
  }

  render() {
    console.log("rendering dropdown");
    return <DropdownMenu trigger={this.props.trigger}>
      {console.log(this.props)}
      {this.props.dropdownItems}
    </DropdownMenu>
  }
}


const DropdownCustomElement = createCustomElement(CustomDropdownMenu, DropdownModel, "container");

customElements.define("dropdown-custom-element", DropdownCustomElement);
