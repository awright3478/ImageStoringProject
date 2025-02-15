# ImageStoringProject
This Data Structure stores Pixelated Images and supports the following operations:
  - Utilizes dynamic programming to highlight 'bluest' or 'most-energy' seam which is calculated based on RGB values
  - 'most-energy' refers to a higher RGB value
  - allows deletion of highlighted seam
  - allows undoing all changes made in chronological order in O(n) time utilizing double-sided linked lists, and stacks
  - has a textual interface (controller) that prompts users for options
