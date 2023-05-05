(ns avisi.apps.tech-testing-ground.prototypes.fulcro.client.atlas-kit.modal
  (:require
    ["@atlaskit/modal-dialog" :default Modal :refer [ModalTransition ModalBody ModalFooter ModalHeader ModalTitle]]
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]) )

(def ui-modal (interop/react-factory Modal))
(def ui-modal-body (interop/react-factory ModalBody))
(def ui-modal-footer (interop/react-factory ModalFooter))
(def ui-modal-header (interop/react-factory ModalHeader))
(def ui-modal-title (interop/react-factory ModalTitle))
(def ui-modal-transition (interop/react-factory ModalTransition))
